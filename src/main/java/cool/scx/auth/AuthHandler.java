package cool.scx.auth;

import cool.scx.annotation.NeedImpl;
import cool.scx.enumeration.Device;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * <p>AuthHandler interface.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
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

    /**
     * <p>login.</p>
     *
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param context  a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json login(String username, String password, RoutingContext context);

    /**
     * <p>info.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json info();

    /**
     * <p>infoUpdate.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json infoUpdate(Map<String, Object> params);

    /**
     * <p>findByUsername.</p>
     *
     * @param username a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json findByUsername(String username);

    /**
     * <p>logout.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json logout();

    /**
     * <p>register.</p>
     *
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json register(String username, String password);
}
