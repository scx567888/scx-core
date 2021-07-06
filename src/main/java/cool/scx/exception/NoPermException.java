package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * <p>NoPermException class.</p>
 *
 * @author scx567888
 * @version 1.1.19
 */
public class NoPermException extends HttpRequestException {

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionHandler(RoutingContext ctx) {
        ctx.response().setStatusCode(403).end("No Perm !!!");
    }

}
