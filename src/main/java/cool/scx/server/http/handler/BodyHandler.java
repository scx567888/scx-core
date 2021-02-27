package cool.scx.server.http.handler;

import cool.scx.config.ScxConfig;
import cool.scx.util.StringUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.FileUploadImpl;
import io.vertx.ext.web.impl.RoutingContextInternal;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>BodyHandler class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class BodyHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BodyHandler.class);
    private static final int DEFAULT_INITIAL_BODY_BUFFER_SIZE = 1024;
    private boolean mergeFormAttributes;
    private boolean deleteUploadedFilesOnEnd;
    private boolean isPreallocateBodyBuffer;


    /**
     * <p>Constructor for BodyHandler.</p>
     */
    public BodyHandler() {
        this.mergeFormAttributes = true;
        this.deleteUploadedFilesOnEnd = false;
        this.isPreallocateBodyBuffer = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();
        if (request.headers().contains(HttpHeaders.UPGRADE, HttpHeaders.WEBSOCKET, true)) {
            context.next();
        } else {
            if (!((RoutingContextInternal) context).seenHandler(2)) {
                long contentLength = this.isPreallocateBodyBuffer ? this.parseContentLengthHeader(request) : -1L;
                BodyHandler.BHandler handler = new BodyHandler.BHandler(context, contentLength);
                request.handler(handler);
                request.endHandler((v) -> {
                    handler.end();
                });
                ((RoutingContextInternal) context).visitHandler(2);
            } else {
                if (this.mergeFormAttributes && request.isExpectMultipart()) {
                    request.params().addAll(request.formAttributes());
                }

                context.next();
            }

        }
    }


    /**
     * <p>Setter for the field <code>mergeFormAttributes</code>.</p>
     *
     * @param mergeFormAttributes a boolean.
     * @return a {@link cool.scx.server.http.handler.BodyHandler} object.
     */
    public BodyHandler setMergeFormAttributes(boolean mergeFormAttributes) {
        this.mergeFormAttributes = mergeFormAttributes;
        return this;
    }

    /**
     * <p>Setter for the field <code>deleteUploadedFilesOnEnd</code>.</p>
     *
     * @param deleteUploadedFilesOnEnd a boolean.
     * @return a {@link cool.scx.server.http.handler.BodyHandler} object.
     */
    public BodyHandler setDeleteUploadedFilesOnEnd(boolean deleteUploadedFilesOnEnd) {
        this.deleteUploadedFilesOnEnd = deleteUploadedFilesOnEnd;
        return this;
    }

    /**
     * <p>setPreallocateBodyBuffer.</p>
     *
     * @param isPreallocateBodyBuffer a boolean.
     * @return a {@link cool.scx.server.http.handler.BodyHandler} object.
     */
    public BodyHandler setPreallocateBodyBuffer(boolean isPreallocateBodyBuffer) {
        this.isPreallocateBodyBuffer = isPreallocateBodyBuffer;
        return this;
    }

    private long parseContentLengthHeader(HttpServerRequest request) {
        String contentLength = request.getHeader(HttpHeaders.CONTENT_LENGTH);
        if (contentLength != null && !contentLength.isEmpty()) {
            try {
                long parsedContentLength = Long.parseLong(contentLength);
                return parsedContentLength < 0L ? -1L : parsedContentLength;
            } catch (NumberFormatException var5) {
                return -1L;
            }
        } else {
            return -1L;
        }
    }

    private class BHandler implements Handler<Buffer> {
        private static final int MAX_PREALLOCATED_BODY_BUFFER_BYTES = 65535;
        final RoutingContext context;
        final long contentLength;
        final boolean isMultipart;
        final boolean isUrlEncoded;
        Buffer body;
        boolean failed;
        AtomicInteger uploadCount = new AtomicInteger();
        AtomicBoolean cleanup = new AtomicBoolean(false);
        boolean ended;
        long uploadSize = 0L;

        public BHandler(RoutingContext context, long contentLength) {
            this.context = context;
            this.contentLength = contentLength;
            if (contentLength != -1L) {
                this.initBodyBuffer();
            }

            Set<FileUpload> fileUploads = context.fileUploads();
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

                context.request().uploadHandler((upload) -> {
                    var s = upload;
                    System.out.println();
                    if (upload.isSizeAvailable()) {
                        long size = this.uploadSize + upload.size();
                        if (size > ScxConfig.bodyLimit) {
                            this.failed = true;
                            this.cancelAndCleanupFileUploads();
                            context.fail(413);
                            return;
                        }
                    }


                    this.uploadCount.incrementAndGet();
//                    String uploadedFileName = (new File(BodyHandler.this.uploadsDir, UUID.randomUUID().toString())).getPath();
                    FileUploadImpl fileUpload = new FileUploadImpl(StringUtils.getUUID(), upload);
                    fileUploads.add(fileUpload);

                    this.uploadEnded();

                });
            }

            context.request().exceptionHandler((t) -> {
                this.cancelAndCleanupFileUploads();
                if (t instanceof DecoderException) {
                    context.fail(400, t.getCause());
                } else {
                    context.fail(t);
                }

            });
        }

        private void initBodyBuffer() {
            int initialBodyBufferSize;
            if (this.contentLength < 0L) {
                initialBodyBufferSize = 1024;
            } else if (this.contentLength > 65535L) {
                initialBodyBufferSize = 65535;
            } else {
                initialBodyBufferSize = (int) this.contentLength;
            }


            initialBodyBufferSize = (int) Math.min(initialBodyBufferSize, ScxConfig.bodyLimit);


            this.body = Buffer.buffer(initialBodyBufferSize);
        }


        public void handle(Buffer buff) {
            if (!this.failed) {
                this.uploadSize += (long) buff.length();
                if (this.uploadSize > ScxConfig.bodyLimit) {
                    this.failed = true;
                    this.cancelAndCleanupFileUploads();
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
            if (this.failed) {
                this.cancelAndCleanupFileUploads();
            } else {
                if (BodyHandler.this.deleteUploadedFilesOnEnd) {
                    this.context.addBodyEndHandler((x) -> {
                        this.cancelAndCleanupFileUploads();
                    });
                }

                HttpServerRequest req = this.context.request();
                if (BodyHandler.this.mergeFormAttributes && req.isExpectMultipart()) {
                    req.params().addAll(req.formAttributes());
                }

                this.context.setBody(this.body);
                this.body = null;
                this.context.next();
            }
        }

        private void cancelAndCleanupFileUploads() {
            if (this.cleanup.compareAndSet(false, true)) {
                Iterator var1 = this.context.fileUploads().iterator();

                while (var1.hasNext()) {
                    FileUpload fileUpload = (FileUpload) var1.next();
                    FileSystem fileSystem = this.context.vertx().fileSystem();
                    if (!fileUpload.cancel()) {
                        String uploadedFileName = fileUpload.uploadedFileName();
                        fileSystem.delete(uploadedFileName, (deleteResult) -> {
                            if (deleteResult.failed()) {
                                BodyHandler.LOG.warn("Delete of uploaded file failed: " + uploadedFileName, deleteResult.cause());
                            }

                        });
                    }
                }
            }

        }
    }
}
