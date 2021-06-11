package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

import java.util.function.Consumer;

/**
 * 自定义 HttpRequestException 异常
 *
 * @author 司昌旭
 * @version 1.1.14
 */
public class CustomHttpRequestException extends HttpRequestException {

    private final Consumer<RoutingContext> errFun;

    /**
     * 自定义异常
     *
     * @param _errFun a long.
     */
    public CustomHttpRequestException(Consumer<RoutingContext> _errFun) {
        this.errFun = _errFun;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionHandler(RoutingContext ctx) {
        errFun.accept(ctx);
    }

}
