package cool.scx._core.auth;

import cool.scx.annotation.ScxMapping;
import cool.scx.auth.ScxAuth;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.enumeration.Method;
import cool.scx.exception.AuthException;
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
     * <p>Constructor for AuthController.</p>
     *
     * @param coreAuthHandler a {@link cool.scx._core.auth.CoreAuthHandler} object
     */
    public AuthController(CoreAuthHandler coreAuthHandler) {
        this.coreAuthHandler = coreAuthHandler;
    }

    /**
     * 登录方法
     *
     * @param params         前台发送的登录数据
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return json
     */
    @ScxMapping(method = Method.POST)
    public Json login(Map<String, Object> params, RoutingContext routingContext) {
        try {
            var loginUser = coreAuthHandler.login(params);
            var loginDevice = ScxAuth.getDevice(ScxContext.routingContext());
            String token = ScxAuth.addAuthUser(routingContext, loginUser);
            if (loginDevice == Device.WEBSITE) {
                return Json.ok("login-successful");
            } else {
                return Json.ok().data("token", token);
            }
        } catch (AuthException authException) {
            return coreAuthHandler.authExceptionHandler(authException);
        }
    }

    /**
     * 注册方法
     *
     * @param params 前台发送的注册信息
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(method = Method.POST)
    public Json signup(Map<String, Object> params) {
        return coreAuthHandler.signup(params);
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
