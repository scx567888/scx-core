package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * 404 not found 未找到异常
 *
 * @author 司昌旭
 * @version 1.1.14
 */
public class NotFoundException extends HttpRequestException {

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionHandler(RoutingContext ctx) {
        ctx.response().setStatusCode(404).send("Not Found !!!");
    }
}
