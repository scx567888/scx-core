package cool.scx.auth;

import cool.scx.enumeration.Device;
import cool.scx.util.Ansi;
import cool.scx.vo.Html;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.HashSet;

/**
 * 权限认证处理器
 *
 * @author 司昌旭
 * @version 1.1.0
 */
public interface AuthHandler {

    /**
     * 未登录 handler
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @param device  a {@link cool.scx.enumeration.Device} object.
     */
    default void noLoginHandler(Device device, RoutingContext context) {
        Ansi.OUT.red("未登录").ln();
        if (device == Device.ADMIN) {
            Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
        } else if (device == Device.ANDROID) {
            Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
        } else if (device == Device.APPLE) {
            Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
        } else if (device == Device.WEBSITE) {
            Html.ofString("未登录").sendToClient(context);
        }
    }

    /**
     * 无权限 handler
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @param device  a {@link cool.scx.enumeration.Device} object.
     */
    default void noPermsHandler(Device device, RoutingContext context) {
        Ansi.OUT.red("没有权限").ln();
        if (device == Device.ADMIN) {
            Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
        } else if (device == Device.ANDROID) {
            Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
        } else if (device == Device.APPLE) {
            Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
        } else if (device == Device.WEBSITE) {
            Html.ofString("没有权限").sendToClient(context);
        }
    }

    /**
     * <p>getAuthUser.</p>
     *
     * @param uniqueID 根据唯一标识 获取 用户
     *                 这里并没有将用户直接存储到 session 中
     *                 而是通过此接口进行查找是为了保证用户信息修改后回显的及时性
     * @return 用户
     */
    AuthUser getAuthUser(String uniqueID);

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户
     * @return s
     */
    HashSet<String> getPerms(AuthUser user);

}
