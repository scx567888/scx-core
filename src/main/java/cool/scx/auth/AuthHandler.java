package cool.scx.auth;

import cool.scx.base.BaseUser;
import cool.scx.enumeration.Device;
import cool.scx.exception.AuthException;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.HashSet;
import java.util.Map;

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
     * 登录接口 成功需要返回一个 登录成功的用户
     * 登录失败请抛出异常
     *
     * @param params 前台发送过来的登录参数
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.AuthException if any.
     */
    BaseUser login(Map<String, Object> params) throws AuthException;

    /**
     * 拉取用户信息
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json info();

    /**
     * 更新信息
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json infoUpdate(Map<String, Object> params);

    /**
     * 退出登录
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json logout();

    /**
     * 登录错误异常处理器
     *
     * @param exception e
     * @return Json
     */
    Json authExceptionHandler(AuthException exception);

    /**
     * 注册用户
     *
     * @param params 注册参数
     * @return a {@link cool.scx.vo.Json} object.
     */
    Json signup(Map<String, Object> params);

    /**
     * <p>findByUsername.</p>
     *
     * @param username 根据用户名获取 用户
     * @return 用户
     */
    BaseUser findByUsername(String username);

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户
     * @return s
     */
    HashSet<String> getPerms(BaseUser user);
}
