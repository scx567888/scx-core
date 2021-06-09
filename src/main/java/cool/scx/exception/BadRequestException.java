package cool.scx.exception;

import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

public class BadRequestException extends HttpRequestException {

    private Throwable throwable;

    public BadRequestException() {

    }

    public BadRequestException(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void exceptionHandler(RoutingContext ctx) {
        if (throwable == null) {
            ctx.response().setStatusCode(400).send("Bad Request !!!");
        } else {
            ctx.response().setStatusCode(400);
            Json.empty().data("error", "Request Parameter Wrong !!!").data("message", throwable.getMessage()).sendToClient(ctx);
        }
    }
}
