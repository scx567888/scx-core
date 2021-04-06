package cool.scx.web.handler;

import cool.scx.enumeration.Device;
import io.vertx.ext.web.RoutingContext;

/**
 * <p>LoginAndPermsHandler interface.</p>
 *
 * @author 司昌旭
 * @version $Id: $Id
 */
public interface LoginAndPermsHandler {

    /**
     * 未登录 handler
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @param device  a {@link cool.scx.enumeration.Device} object.
     */
    void noLogin(Device device, RoutingContext context);


    /**
     * 无权限 handler
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @param device  a {@link cool.scx.enumeration.Device} object.
     */
    void noPerms(Device device, RoutingContext context);

}
