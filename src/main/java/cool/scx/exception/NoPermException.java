package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

public class NoPermException extends HttpRequestException {

    @Override
    public void exceptionHandler(RoutingContext ctx) {
        ctx.response().setStatusCode(403).send("No Perm !!!");
    }

}
