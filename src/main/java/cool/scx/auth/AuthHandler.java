package cool.scx.auth;

import cool.scx.enumeration.DeviceType;
import cool.scx.exception.NoPermException;
import cool.scx.exception.UnauthorizedException;
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
     * @param device  a {@link cool.scx.enumeration.DeviceType} object.
     */
    default void noLoginHandler(DeviceType device, RoutingContext context) throws Exception {
        throw new UnauthorizedException();
    }

    /**
     * 无权限 handler
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @param device  a {@link cool.scx.enumeration.DeviceType} object.
     */
    default void noPermsHandler(DeviceType device, RoutingContext context) throws Exception {
        throw new NoPermException();
    }

    /**
     * 根据唯一标识 获取 用户
     * <p>
     * 这里并没有将用户直接存储到 session 中
     * <p>
     * 而是通过此接口进行查找是为了保证用户信息修改后回显的及时性
     *
     * @param uniqueID 唯一 ID 可以是用户名,手机号之类
     * @return 用户
     */
    AuthUser getAuthUser(String uniqueID);

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户 (这里只会使用用户的唯一标识 所以其他的字段可以为空)
     * @return 权限字符串集合
     */
    HashSet<String> getPerms(AuthUser user);

}
