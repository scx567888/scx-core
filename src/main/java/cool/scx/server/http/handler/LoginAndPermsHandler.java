package cool.scx.server.http.handler;

import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

/**
 * <p>LoginAndPermsHandler interface.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public interface LoginAndPermsHandler {

    /**
     * <p>noLogin.</p>
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     */
    default void noLoginByCookie(RoutingContext context) {
        Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
    }

    /**
     * <p>noLoginByHeader.</p>
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     */
    default void noLoginByHeader(RoutingContext context) {
        Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
    }

    /**
     * <p>noPerms.</p>
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     */
    default void noPermsByCookie(RoutingContext context) {
        Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
    }

    /**
     * <p>noPermsByHeader.</p>
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     */
    default void noPermsByHeader(RoutingContext context) {
        Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
    }

}
