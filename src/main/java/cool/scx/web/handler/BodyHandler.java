package cool.scx.web.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextInternal;

/**
 * <p>BodyHandler class.</p>
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class BodyHandler implements Handler<RoutingContext> {


    /**
     * <p>Constructor for BodyHandler.</p>
     */
    public BodyHandler() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();
        //如果 连接头包含 upgrade 或者 websocket 就不做处理 直接跳转到下一个handler
        if (request.headers().contains(HttpHeaders.UPGRADE, HttpHeaders.WEBSOCKET, true)) {
            context.next();
        } else {
            if (!((RoutingContextInternal) context).seenHandler(2)) {
                var handler = new BHandler(context, parseContentLengthHeader(request));
                request.handler(handler);
                request.endHandler((v) -> {
                    handler.end();
                });
                ((RoutingContextInternal) context).visitHandler(2);
            } else {
                context.next();
            }
        }
    }

    /**
     * 获取 ContentLength
     *
     * @param request
     * @return
     */
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

}
