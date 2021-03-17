package cool.scx.web.handler;

import cool.scx.enumeration.Device;
import cool.scx.util.Ansi;
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
     * @param device  a {@link cool.scx.enumeration.Device} object.
     */
    default void noLogin(Device device, RoutingContext context) {
        if (device == Device.ADMIN) {
            Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
        } else if (device == Device.ANDROID) {
            Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
        } else if (device == Device.APPLE) {
            Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
        } else if (device == Device.WEBSITE) {
            Ansi.OUT.red("未登录").ln();
        }
    }

    /**
     * <p>noPerms.</p>
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @param device  a {@link cool.scx.enumeration.Device} object.
     */
    default void noPerms(Device device, RoutingContext context) {
        if (device == Device.ADMIN) {
            Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
        } else if (device == Device.ANDROID) {
            Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
        } else if (device == Device.APPLE) {
            Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
        } else if (device == Device.WEBSITE) {
            Ansi.OUT.red("没有权限").ln();
        }
    }

}
