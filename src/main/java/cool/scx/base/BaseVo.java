package cool.scx.base;

import io.vertx.ext.web.RoutingContext;

/**
 * <p>BaseVo interface.</p>
 *
 * @author 司昌旭
 * @version 0.5.0
 */
public interface BaseVo {
    /**
     * <p>sendToClient.</p>
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @throws java.lang.Exception if any.
     */
    void sendToClient(RoutingContext context) throws Exception;
}
