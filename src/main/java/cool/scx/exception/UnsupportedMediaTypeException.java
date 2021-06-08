package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * 415 参数异常
 */
public class UnsupportedMediaTypeException extends HttpRequestException {

    @Override
    public void exceptionHandler(RoutingContext ctx) {
        ctx.response().setStatusCode(415).send("Request Parameter Wrong!!!");
    }

}
