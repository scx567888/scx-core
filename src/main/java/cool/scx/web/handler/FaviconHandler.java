package cool.scx.web.handler;

import cool.scx.config.ScxConfig;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 注册 FaviconIco 图标 handler
 */
public class FaviconHandler implements Handler<RoutingContext> {
    private final long maxAgeSeconds = 86400;
    private boolean faviconExists = false;
    private Buffer imgBuffer = null;
    private String bufferLength = null;

    public FaviconHandler() {
        var faviconPath = Path.of(ScxConfig.cmsRoot().getPath(), "favicon.ico");
        if (Files.exists(faviconPath)) {
            faviconExists = true;
            try {
                byte[] bytes = Files.readAllBytes(faviconPath);
                imgBuffer = Buffer.buffer(bytes);
                bufferLength = Integer.toString(imgBuffer.length());
            } catch (IOException e) {
                e.printStackTrace();
                faviconExists = false;
            }
        }
    }

    @Override
    public void handle(RoutingContext ctx) {
        if ("/favicon.ico".equals(ctx.request().path())) {
            var resp = ctx.response();
            if (!faviconExists) {
                resp.setStatusCode(404).end();
            } else {
                //todo 支持 svg 图标
                resp.headers().add(HttpHeaders.CONTENT_TYPE, "image/x-icon");
                resp.headers().add(HttpHeaders.CONTENT_LENGTH, bufferLength);
                resp.headers().add(HttpHeaders.CACHE_CONTROL, "public, max-age=" + maxAgeSeconds);
                resp.end(imgBuffer);
            }
        } else {
            ctx.next();
        }
    }
}
