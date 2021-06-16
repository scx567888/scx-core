package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * 未认证异常 (未登录)
 */
public class UnauthorizedException extends HttpRequestException {

    @Override
    public void exceptionHandler(RoutingContext ctx) {
        ctx.response().setStatusCode(401).send("Unauthorized !!!");
    }

}
