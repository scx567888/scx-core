package cool.scx.auth;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.enumeration.Method;
import cool.scx.exception.AuthException;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;

import java.util.Map;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class AuthController {

    /**
     * 登录方法
     *
     * @param params 前台发送的登录数据
     * @return json
     */
    @ScxMapping(method = Method.POST)
    public Json login(Map<String, Object> params) {
        try {
            var device = ScxContext.device();
            var loginUser = ScxAuth.authHandler().login(params);
            if (device == Device.ADMIN || device == Device.APPLE || device == Device.ANDROID) {
                var token = StringUtils.getUUID();
                ScxAuth.addLoginItem(device, token, loginUser.username);
                //返回登录用户的 Token 给前台，角色和权限信息通过 auth/info 获取
                return Json.ok().data("token", token);
            } else if (device == Device.WEBSITE) {
                String token = ScxAuth.getTokenByCookie();
                ScxAuth.addLoginItem(device, token, loginUser.username);
                return Json.ok("login-successful");
            } else {
                return Json.ok("unknown-device");
            }
        } catch (AuthException authException) {
            return ScxAuth.authHandler().authExceptionHandler(authException);
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
        return ScxAuth.authHandler().signup(params);
    }

    /**
     * 退出登录方法 同时清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(method = Method.POST)
    public Json logout() {
        return ScxAuth.authHandler().logout();
    }


    /**
     * 拉取当前登录用户的信息 (包括权限)
     *
     * @return Json
     */
    @ScxMapping(method = Method.GET)
    public Json info() {
        return ScxAuth.authHandler().info();
    }

    /**
     * 用户自己更新的信息 (不包括权限)
     *
     * @param params 用户信息
     * @return Json
     */
    @ScxMapping(method = Method.POST)
    public Json infoUpdate(Map<String, Object> params) {
        return ScxAuth.authHandler().infoUpdate(params);
    }

}
