package cool.scx._core._auth.auth;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.Method;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxMapping
public class AuthController {

    /**
     * handler
     */
    private final CoreAuthHandler coreAuthHandler;

    /**
     * a
     *
     * @param coreAuthHandler a
     */
    public AuthController(CoreAuthHandler coreAuthHandler) {
        this.coreAuthHandler = coreAuthHandler;
    }

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @param ctx      a
     * @return json
     */
    @ScxMapping(method = Method.POST)
    public Json login(@FromBody String username, @FromBody String password, RoutingContext ctx) {
        return coreAuthHandler.login(username, password, ctx);
    }

    /**
     * 注册方法
     *
     * @param username 前台发送的用户名
     * @param password 前台发送的密码
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(method = Method.POST)
    public Json signup(String username, String password) {
        return coreAuthHandler.signup(username, password);
    }

    /**
     * 退出登录方法 同时清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(method = Method.POST)
    public Json logout() {
        return coreAuthHandler.logout();
    }

    /**
     * 拉取当前登录用户的信息 (包括权限)
     *
     * @return Json
     */
    @ScxMapping(method = Method.GET)
    public Json info() {
        return coreAuthHandler.info();
    }

    /**
     * 用户自己更新的信息 (不包括权限)
     *
     * @param params 用户信息
     * @return Json
     */
    @ScxMapping(method = Method.POST)
    public Json infoUpdate(Map<String, Object> params) {
        return coreAuthHandler.infoUpdate(params);
    }

}
