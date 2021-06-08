package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * 这个错误会被 handler 进行截获并调用 这里自定义的错误处理程序 包括设置状态码 或者返回一些其他的东西之类的
 * 注意 错误处理程序中不需要进行 end  end 会在 handler 中自动调用
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public abstract class HttpRequestException extends Exception {

    /**
     * 异常 handler
     * @param ctx
     */
    public abstract void exceptionHandler(RoutingContext ctx);

}
