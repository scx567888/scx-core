package cool.scx.auth;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Method;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * <p>UserController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController("api/auth")
public class AuthController {

    private final AuthHandler authHandler;

    /**
     * <p>Constructor for AuthController.</p>
     */
    public AuthController() {
        this.authHandler = ScxContext.getBean(AuthHandler.class);
    }

    /**
     * 登录方法
     * 此处有一个限制 若数据库中没有任何用户 为了防止
     * 系统无法登录 此处新建一个用户 名为 admin 密码为 password 的超级管理员用户
     *
     * @param username 用户 包含用户名和密码
     * @param password 密码
     * @param context  a {@link io.vertx.ext.web.RoutingContext} object.
     * @return json
     */
    @ScxMapping(method = Method.POST)
    public Json login(@FromBody("username") String username, @FromBody("password") String password, RoutingContext context) {
        return authHandler.login(username, password, context);
    }

    /**
     * 拉取用户信息
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = "info", method = Method.GET)
    public Json info() {
        return authHandler.info();
    }

    /**
     * <p>register.</p>
     *
     * @param username a {@link java.util.Map} object.
     * @param password a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(method = Method.POST)
    public Json register(String username, String password) {
        return authHandler.register(username, password);
    }

    /**
     * 退出登录方法 清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(method = Method.POST)
    public Json logout() {
        return authHandler.logout();
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 查询的用户名
     * @return 是否查找到
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json findByUsername(String username) {
        return authHandler.findByUsername(username);
    }

    /**
     * 用户自己更新的信息
     *
     * @param params 用户信息
     * @return 通知
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json infoUpdate(Map<String, Object> params) {
        return authHandler.infoUpdate(params);
    }

}
