package cool.scx._core.auth.impl;

import cool.scx.annotation.ScxService;
import cool.scx.auth.AuthHandler;
import cool.scx.auth.User;
import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.enumeration.SortType;
import cool.scx.exception.AuthException;
import cool.scx.exception.TooManyErrorsException;
import cool.scx.exception.UnknownUserException;
import cool.scx.exception.WrongPasswordException;
import cool.scx.util.Ansi;
import cool.scx.util.LogUtils;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * 核心包的权限处理器
 * 如有需求可以自行实现 AuthHandler 接口
 *
 * @author 司昌旭
 * @version 1.0.10
 */
@ScxService
public class CoreAuthHandler implements AuthHandler {

    private final CoreUserService coreUserService;

    public CoreAuthHandler(CoreUserService coreUserService) {
        this.coreUserService = coreUserService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void noLogin(Device device, RoutingContext context) {
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
     * {@inheritDoc}
     */
    @Override
    public void noPerms(Device device, RoutingContext context) {
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

    @Override
    public Json login(String username, String password, RoutingContext context) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return Json.fail(StringUtils.isEmpty(username) ? "用户名不能为空" : "密码不能为空");
        }
        //此处验证需要使用拦截器进行处理
//        if (!licenseService.passLicense()) {
//            return Json.fail(Json.FAIL_CODE, "licenseError");
//        }
        try {
            var device = ScxContext.device();
            //登录
            var loginUser = coreUserService.login(username, password);
            if (device == Device.ADMIN || device == Device.APPLE || device == Device.ANDROID) {
                var token = StringUtils.getUUID();
                ScxContext.addLoginItem(device, token, loginUser.username);
                //返回登录用户的 Token 给前台，角色和权限信息通过 auth/info 获取
                return Json.ok().data("token", token);
            } else if (device == Device.WEBSITE) {
                String value = ScxContext.routingContext().getCookie(ScxConfig.tokenKey()).getValue();
                ScxContext.addLoginItem(device, value, loginUser.username);
                return Json.ok("登录成功");
            } else {
                return Json.ok("登录设备未知 !!!");
            }
        } catch (UnknownUserException uue) {
            return Json.fail(ScxConfig.confusionLoginError() ? "usernameOrPasswordError" : "userNotFound");
        } catch (WrongPasswordException wpe) {
            //这里和用户密码错误   可以使用相同的 提示信息 防止恶意破解
            return Json.fail(ScxConfig.confusionLoginError() ? "usernameOrPasswordError" : "passwordError");
        } catch (TooManyErrorsException tee) {
            //密码错误次数过多
            return Json.fail("tooManyErrors").data("remainingTime", tee.remainingTime);
        } catch (AuthException ae) {
            LogUtils.recordLog("登录出错 : " + ae.getMessage(), "");
            return Json.fail("logonFailure");
        } catch (Exception e) {
            LogUtils.recordLog("密码加密校验出错 : " + e.getMessage(), "");
            return Json.fail("logonFailure");
        }
    }

    @Override
    public Json info() {
        var user = ScxContext.getLoginUser();
        //从session取出用户信息
        if (user == null) {
            return Json.fail(Json.ILLEGAL_TOKEN, "登录已失效");
        } else {
            //返回登录用户的信息给前台 含用户的所有角色和权限
            var permList = coreUserService.getPermStrByUser(user);
            return Json.ok()
                    .data("id", user.id)
                    .data("username", user.username)
                    .data("nickName", user.nickName)
                    .data("avatar", user.avatar)
                    .data("perms", permList)
                    .data("realDelete", ScxConfig.realDelete());
        }
    }

    @Override
    public Json infoUpdate(Map<String, Object> params) {
        var queryUser = ObjectUtils.mapToBean(params, User.class);
        var currentUser = ScxContext.getLoginUser();
        currentUser.nickName = queryUser.nickName;
        currentUser.phone = queryUser.phone;
        currentUser.password = queryUser.password;
        currentUser.salt = null;
        var b = coreUserService.updateUserPassword(currentUser) != null;
        LogUtils.recordLog("更新了自己的信息", "用户名是 :" + currentUser.username);
        return Json.ok().data("success", b);
    }

    @Override
    public Json findByUsername(String username) {
        var user = coreUserService.findByUsername(username);
        if (user == null) {
            return Json.ok().data("success", true);
        } else {
            return Json.ok().data("success", false);
        }
    }

    @Override
    public Json logout() {
        ScxContext.removeLoginUser();
        return Json.ok("User Logged Out");
    }

    @Override
    public Json register(String username, String password) {
        var newUser = new Param<>(new CoreUser());

        newUser.addOrderBy("id", SortType.ASC).queryObject.username = username;

        User user = coreUserService.get(newUser);
        if (user != null) {
            return Json.ok("userAlreadyExists");
        } else {
            newUser.queryObject.level = 4;
            newUser.queryObject.password = password;
            coreUserService.registeredUser(newUser.queryObject);
            return Json.ok("registerSuccess");
        }
    }

}
