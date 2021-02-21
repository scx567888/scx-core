package cool.scx.base;

import io.vertx.ext.web.RoutingContext;

/**
 * <p>BaseVo interface.</p>
 *
 * @author 司昌旭
 * @version 0.5.0
 */
public interface BaseVo {
    void sendToClient(RoutingContext context) throws Exception;
}
