package cool.scx.exception;

import io.vertx.ext.web.RoutingContext;

/**
 * 在 ScxMapping 注解标记的方法中抛出此异常会被ScxMappingHandler 进行截获并调用其中的 {@link #exceptionHandler}
 * <p>
 * 当我们的代码中有需要向客户端返回错误信息的时候
 * <p>
 * 推荐创建 HttpRequestException 的实现类并抛出异常 , 而不是手动进行异常的处理与响应的返回
 *
 * @author scx567888
 * @version 1.0.10
 */
public abstract class HttpRequestException extends Exception {

    /**
     * 异常 handler
     * <p>
     * 可以进行设置状态码 重定向之类等操作
     * <p>
     * 注意 方法中不需要进行 end  end 会在 会被ScxMappingHandler 中自动调用
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object
     */
    public abstract void exceptionHandler(RoutingContext ctx);

}
