package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * 415 参数异常
 *
 * @author scx567888
 * @version 1.1.14
 */
public class UnsupportedMediaTypeException extends HttpRequestException {

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionHandler(RoutingContext ctx) {
        ctx.response().setStatusCode(415).send("Unsupported Media Type !!!");
    }

}
