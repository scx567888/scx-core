package cool.scx.auth;

import cool.scx.enumeration.Device;
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
    void noLoginHandler(Device device, RoutingContext context);

    /**
     * 无权限 handler
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @param device  a {@link cool.scx.enumeration.Device} object.
     */
    void noPermsHandler(Device device, RoutingContext context);

    /**
     * <p>getAuthUser.</p>
     *
     * @param username 根据用户名获取 用户
     *                 这里并没有将用户直接存储到 session 中
     *                 而是通过此接口进行查找是为了保证用户信息修改后回显的及时性
     * @return 用户
     */
    AuthUser getAuthUser(String username);

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户
     * @return s
     */
    HashSet<String> getPerms(AuthUser user);

}
