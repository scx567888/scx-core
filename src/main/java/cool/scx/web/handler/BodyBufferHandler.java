package cool.scx.web.handler;

import cool.scx.bo.FileUpload;
import cool.scx.util.FileUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

class BodyBufferHandler implements Handler<Buffer> {

    private static final long BODY_LIMIT = FileUtils.displaySizeToLong("16384KB");
    final RoutingContext context;
    final long contentLength;
    final boolean isMultipart;
    final boolean isUrlEncoded;
    Buffer body;
    boolean failed;
    AtomicInteger uploadCount = new AtomicInteger();
    boolean ended;
    long uploadSize = 0L;

    /**
     * <p>Constructor for BHandler.</p>
     *
     * @param context       a {@link io.vertx.ext.web.RoutingContext} object.
     * @param contentLength a long.
     */
    public BodyBufferHandler(RoutingContext context, long contentLength) {
        this.context = context;
        this.contentLength = contentLength;
        if (contentLength != -1L) {
            this.initBodyBuffer();
        }

        String contentType = context.request().getHeader(HttpHeaders.CONTENT_TYPE);
        if (contentType == null) {
            this.isMultipart = false;
            this.isUrlEncoded = false;
        } else {
            String lowerCaseContentType = contentType.toLowerCase();
            this.isMultipart = lowerCaseContentType.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString());
            this.isUrlEncoded = lowerCaseContentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
        }

        if (this.isMultipart || this.isUrlEncoded) {
            context.request().setExpectMultipart(true);

            var uploadFiles = new HashSet<FileUpload>();
            context.request().uploadHandler((upload) -> {
                var tempBuffer = Buffer.buffer();
                upload.handler(tempBuffer::appendBuffer);
                uploadFiles.add(new FileUpload(upload.name(), upload.filename(), (long) tempBuffer.length(), tempBuffer));
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

        context.request().exceptionHandler((t) -> {
            if (t instanceof DecoderException) {
                context.fail(400, t.getCause());
            } else {
                context.fail(t);
            }

        });
    }

    /**
     * 初始化  body
     */
    private void initBodyBuffer() {
        int initialBodyBufferSize;
        if (this.contentLength < 0L) {
            initialBodyBufferSize = 1024;
        } else if (this.contentLength > 65535L) {
            initialBodyBufferSize = 65535;
        } else {
            initialBodyBufferSize = (int) this.contentLength;
        }

        initialBodyBufferSize = (int) Math.min(initialBodyBufferSize, BODY_LIMIT);

        this.body = Buffer.buffer(initialBodyBufferSize);
    }


    /**
     * <p>handle.</p>
     *
     * @param buff a {@link io.vertx.core.buffer.Buffer} object.
     */
    public void handle(Buffer buff) {
        if (!this.failed) {
            this.uploadSize += buff.length();
            if (this.uploadSize > BODY_LIMIT) {
                this.failed = true;
                this.context.fail(413);
            } else if (!this.isMultipart) {
                if (this.body == null) {
                    this.initBodyBuffer();
                }
                this.body.appendBuffer(buff);
            }
        }
    }

    void uploadEnded() {
        int count = this.uploadCount.decrementAndGet();
        if (this.ended && count == 0) {
            this.doEnd();
        }
    }

    void end() {
        this.ended = true;
        if (this.uploadCount.get() == 0) {
            this.doEnd();
        }
    }

    void doEnd() {
        if (!this.failed) {
            this.context.setBody(this.body);
            this.body = null;
            this.context.next();
        }
    }

}
