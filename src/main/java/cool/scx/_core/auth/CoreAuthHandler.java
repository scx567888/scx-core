package cool.scx._core.auth;

import cool.scx._core.auth.exception.*;
import cool.scx._core.config.CoreConfig;
import cool.scx._core.user.User;
import cool.scx._core.user.UserService;
import cool.scx.annotation.ScxService;
import cool.scx.auth.AuthHandler;
import cool.scx.auth.AuthUser;
import cool.scx.auth.ScxAuth;
import cool.scx.auth.exception.UnknownDeviceException;
import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.enumeration.Device;
import cool.scx.enumeration.SortType;
import cool.scx.exception.AuthException;
import cool.scx.util.*;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
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

    private static final HashMap<String, LoginError> loginErrorMap = new HashMap<>();

    private final UserService coreUserService;

    /**
     * <p>Constructor for CoreAuthHandler.</p>
     *
     * @param coreUserService a {@link UserService} object.
     */
    public CoreAuthHandler(UserService coreUserService) {
        this.coreUserService = coreUserService;
    }


    /**
     * info
     */
    public Json info() {
        var user = (User) ScxAuth.getLoginUser();
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

    /**
     * {@inheritDoc}
     */
    public Json infoUpdate(Map<String, Object> params) {
        var queryUser = ObjectUtils.mapToBean(params, User.class);
        var currentUser = (User) ScxAuth.getLoginUser();
        queryUser.id = currentUser.id;
        currentUser.password = queryUser.password;
        currentUser.salt = null;
        var b = coreUserService.updateUserPassword(currentUser) != null;
        LogUtils.recordLog("更新了自己的信息", "用户名是 :" + currentUser.username);
        return Json.ok().data("success", b);
    }

    /**
     * logout
     */
    public Json logout() {
        var b = ScxAuth.removeAuthUser();
        if (b) {
            return Json.ok("User Logged Out");
        } else {
            return Json.fail("User Logged Out Fail");
        }
    }

    /**
     * authExceptionHandler
     */
    public Json authExceptionHandler(AuthException e) {
        if (e instanceof UnknownDeviceException) {
            return Json.fail("未知设备");
        } else if (e instanceof EmptyUsernameException) {
            return Json.fail("用户名不能为空");
        } else if (e instanceof EmptyPasswordException) {
            return Json.fail("密码不能为空");
        } else if (e instanceof UnknownUserException) {
            return Json.fail(CoreConfig.confusionLoginError() ? "usernameOrPasswordError" : "userNotFound");
        } else if (e instanceof WrongPasswordException) {
            //这里和用户密码错误   可以使用相同的 提示信息 防止恶意破解
            return Json.fail(CoreConfig.confusionLoginError() ? "usernameOrPasswordError" : "passwordError");
        } else if (e instanceof TooManyErrorsException) {
            //密码错误次数过多
            return Json.fail("tooManyErrors").data("remainingTime", ((TooManyErrorsException) e).remainingTime);
        } else {
            LogUtils.recordLog("登录出错 : " + e.getMessage(), "");
            return Json.fail("logonFailure");
        }
    }

    /**
     * signup
     */
    public Json signup(Map<String, Object> params) {
        var username = params.get("username").toString();
        var password = params.get("password").toString();
        var newUser = new Param<>(new User());

        newUser.addOrderBy("id", SortType.ASC).queryObject.username = username;

        AuthUser user = coreUserService.get(newUser);
        if (user != null) {
            return Json.ok("userAlreadyExists");
        } else {
            newUser.queryObject.level = 4;
            newUser.queryObject.password = password;
            coreUserService.registeredUser(newUser.queryObject);
            return Json.ok("registerSuccess");
        }
    }

    /**
     * {@inheritDoc}
     */
    public AuthUser findByUsername(String username) {
        return coreUserService.findByUsername(username);
    }

    @Override
    public HashSet<String> getPerms(AuthUser user) {
        return coreUserService.getPermStrByUser((User) user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void noLoginHandler(Device device, RoutingContext context) {
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
    public void noPermsHandler(Device device, RoutingContext context) {
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
    public AuthUser getAuthUser(String username) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public AuthUser login(Map<String, Object> params) throws AuthException {
        //license 失效时不允许登录
//        if (!licenseService.passLicense()) {
//            return Json.fail(Json.FAIL_CODE, "licenseError");
//        }
        var username = params.get("username");
        var password = params.get("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            if (StringUtils.isEmpty(username)) {
                throw new EmptyUsernameException();
            } else {
                throw new EmptyPasswordException();
            }
        }
        var now = LocalDateTime.now();
        var ip = NetUtils.getIpAddr();
        var loginError = loginErrorMap.get(ip);
        if (loginError == null) {
            var le = new LoginError(LocalDateTime.now(), 0);
            loginErrorMap.put(ip, le);
            loginError = le;
        }
        if (notHaveLoginError(ip, loginError)) {
            var user = (User) coreUserService.findByUsername(username.toString());
            if (user == null) {
                var le = new LoginError(now, loginError.errorTimes + 1);
                loginErrorMap.put(ip, le);
                throw new UnknownUserException();
            }
            if (!verifyPassword(user, password.toString())) {
                var le = new LoginError(now, loginError.errorTimes + 1);
                loginErrorMap.put(ip, le);
                throw new WrongPasswordException();
            }
            return user;
        } else {
            LogUtils.recordLog(ip + " : 错误登录次数过多");
            var duration = Duration.between(now, loginError.lastErrorDate).toSeconds();
            throw new TooManyErrorsException(duration);
        }
    }


    private boolean notHaveLoginError(String ip, LoginError loginError) {
        if (LocalDateTime.now().isBefore(loginError.lastErrorDate)) {
            return false;
        } else if (loginError.errorTimes >= CoreConfig.loginErrorLockTimes()) {
            LoginError le = new LoginError(LocalDateTime.now().plusSeconds(CoreConfig.loginErrorLockSecond()), 0);
            loginErrorMap.put(ip, le);
            return false;
        }
        return true;
    }

    /**
     * 校验密码是否正确
     *
     * @param user     用户包括密码和盐
     * @param password 前台传过来密码
     * @return 是否相同
     */
    private boolean verifyPassword(User user, String password) {
        try {
            var decryptPassword = CryptoUtils.decryptPassword(user.password, user.salt);
            return password.equals(decryptPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
