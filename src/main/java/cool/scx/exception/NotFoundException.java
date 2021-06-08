package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * 404 not found 未找到异常
 */
public class NotFoundException extends HttpRequestException {
    @Override
    public void exceptionHandler(RoutingContext ctx) {
        ctx.response().setStatusCode(404).send("Not Found!!!");
    }
}
