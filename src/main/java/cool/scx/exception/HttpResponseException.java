package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

import java.util.function.Consumer;

public class HttpResponseException extends Exception {

    public final Consumer<RoutingContext> errFun;

    /**
     * <p>Constructor for TooManyErrorsException.</p>
     *
     * @param _errFun a long.
     */
    public HttpResponseException(Consumer<RoutingContext> _errFun) {
        this.errFun = _errFun;
    }
}
