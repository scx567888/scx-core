package cool.scx.exception;

import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

/**
 * 请求错误异常
 *
 * @author scx567888
 * @version 1.1.15
 */
public class BadRequestException extends HttpRequestException {

    private Throwable throwable;

    /**
     * <p>Constructor for BadRequestException.</p>
     */
    public BadRequestException() {

    }

    /**
     * <p>Constructor for BadRequestException.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object
     */
    public BadRequestException(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionHandler(RoutingContext ctx) {
        if (throwable == null) {
            ctx.response().setStatusCode(400).end("Bad Request !!!");
        } else {
            ctx.response().setStatusCode(400);
            Json.fail("Request Parameter Wrong !!!").put("err-message", throwable.getMessage()).sendToClient(ctx);
        }
    }
}
