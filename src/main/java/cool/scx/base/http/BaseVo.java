package cool.scx.base.http;

import io.vertx.ext.web.RoutingContext;

/**
 * BaseVo 接口
 * 所有需要向前台返回数据都需要基础
 *
 * @author 司昌旭
 * @version 0.5.0
 */
public interface BaseVo {
    /**
     * 向客户端发送相应的方法
     *
     * @param context 上下文对象
     * @throws java.lang.Exception if any.
     */
    void sendToClient(RoutingContext context) throws Exception;
}
