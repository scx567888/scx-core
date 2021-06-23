package cool.scx.web.handler;

import cool.scx.bo.FileUpload;
import cool.scx.util.FileUtils;
import io.netty.handler.codec.DecoderException;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

class BodyBufferHandler implements Handler<Buffer> {

    private static final long BODY_LIMIT = FileUtils.displaySizeToLong("16384KB");
    final RoutingContext context;
    final boolean isMultipart;
    final boolean isUrlEncoded;
    final AtomicInteger uploadCount = new AtomicInteger();
    Buffer body = Buffer.buffer(1024);
    boolean failed;
    boolean ended;
    long uploadSize = 0L;

    /**
     * <p>Constructor for BodyBufferHandler.</p>
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object
     */
    BodyBufferHandler(RoutingContext context) {
        this.context = context;
        var contentType = context.request().getHeader(HttpHeaders.CONTENT_TYPE);

        this.isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/form-data");
        this.isUrlEncoded = contentType != null && contentType.toLowerCase().startsWith("application/x-www-form-urlencoded");

        if (this.isMultipart || this.isUrlEncoded) {
            context.request().setExpectMultipart(true);
            var uploadFiles = new HashSet<FileUpload>();
            context.request().uploadHandler(upload -> {
                var tempBuffer = Buffer.buffer();
                upload.handler(tempBuffer::appendBuffer);
                uploadFiles.add(new FileUpload(upload.name(), upload.filename(), tempBuffer));
                if (upload.isSizeAvailable()) {
                    long size = this.uploadSize + upload.size();
                    if (size > BODY_LIMIT) {
                        this.failed = true;
                        context.fail(413);
                        return;
                    }
                }
                this.uploadCount.incrementAndGet();

                this.uploadEnded();

            });
            context.put("uploadFiles", uploadFiles);
        }

        context.request().exceptionHandler(t -> {
            if (t instanceof DecoderException) {
                context.fail(400, t.getCause());
            } else {
                context.fail(t);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Buffer buff) {
        if (!this.failed) {
            this.uploadSize += buff.length();
            if (this.uploadSize > BODY_LIMIT) {
                this.failed = true;
                this.context.fail(413);
            } else if (!this.isMultipart) {
                if (this.body == null) {
                    this.body = Buffer.buffer(1024);
                }
                this.body.appendBuffer(buff);
            }
        }
    }

    private void uploadEnded() {
        int count = this.uploadCount.decrementAndGet();
        if (this.ended && count == 0) {
            this.doEnd();
        }
    }

    /**
     * <p>end.</p>
     */
    public void end() {
        this.ended = true;
        if (this.uploadCount.get() == 0) {
            this.doEnd();
        }
    }

    private void doEnd() {
        if (!this.failed) {
            this.context.setBody(this.body);
            this.body = null;
            this.context.next();
        }
    }

}
