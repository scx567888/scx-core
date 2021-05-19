package cool.scx.auth;

import cool.scx.annotation.NeedImpl;
import cool.scx.enumeration.Device;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

@NeedImpl
public interface AuthHandler {

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

    Json login(String username, String password, RoutingContext context);

    Json info();

    Json infoUpdate(Map<String, Object> params);

    Json findByUsername(String username);

    Json logout();

    Json register(String username, String password);
}
