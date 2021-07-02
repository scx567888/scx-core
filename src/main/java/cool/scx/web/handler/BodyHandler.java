package cool.scx.web.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * 处理 body 请求体处理器
 *
 * <p>BodyHandler class.</p>
 *
 * @author scx567888
 * @version 1.0.10
 */
public class BodyHandler implements Handler<RoutingContext> {

    /**
     * {@inheritDoc}
     * <p>
     * handle
     */
    @Override
    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();
        if (request.headers().contains(HttpHeaders.UPGRADE, HttpHeaders.WEBSOCKET, true)) {
            context.next();
        } else {
            var handler = new BodyBufferHandler(context);
            request.handler(handler);
            request.endHandler(v -> handler.end());
        }
    }

}
