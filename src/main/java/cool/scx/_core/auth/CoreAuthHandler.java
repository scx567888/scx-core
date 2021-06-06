package cool.scx._core.auth;

import cool.scx._core.auth.exception.*;
import cool.scx._core.config.CoreConfig;
import cool.scx._core.dept.DeptService;
import cool.scx._core.log.LogUtils;
import cool.scx._core.role.RoleService;
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
    private final UserService userService;
    private final RoleService roleService;
    private final DeptService deptService;

    /**
     * <p>Constructor for CoreAuthHandler.</p>
     *
     * @param userService a {@link cool.scx._core.user.UserService} object
     * @param roleService a {@link cool.scx._core.role.RoleService} object
     * @param deptService a {@link cool.scx._core.dept.DeptService} object
     */
    public CoreAuthHandler(UserService userService, RoleService roleService, DeptService deptService) {
        this.userService = userService;
        this.roleService = roleService;
        this.deptService = deptService;
    }

    /**
     * info
     *
     * @return a {@link cool.scx.vo.Json} object
     */
    public Json info() {
        var user = (User) ScxAuth.getLoginUser();
        //从session取出用户信息
        if (user == null) {
            return Json.fail(Json.ILLEGAL_TOKEN, "登录已失效");
        } else {
            //返回登录用户的信息给前台 含用户的所有角色和权限
            var permList = getPermsByUser(user);
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
     *
     * @param params a {@link java.util.Map} object
     * @return a {@link cool.scx.vo.Json} object
     */
    public Json infoUpdate(Map<String, Object> params) {
        var queryUser = ObjectUtils.mapToBean(params, User.class);
        var currentUser = (User) ScxAuth.getLoginUser();
        queryUser.id = currentUser.id;
        currentUser.password = queryUser.password;
        currentUser.salt = null;
        var b = updateUserPassword(currentUser) != null;
        LogUtils.recordLog("更新了自己的信息", "用户名是 :" + currentUser.username);
        return Json.ok().data("success", b);
    }

    /**
     * logout
     *
     * @return a {@link cool.scx.vo.Json} object
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
     *
     * @param e a {@link cool.scx.exception.AuthException} object
     * @return a {@link cool.scx.vo.Json} object
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
     *
     * @param params a {@link java.util.Map} object
     * @return a {@link cool.scx.vo.Json} object
     */
    public Json signup(Map<String, Object> params) {
        var username = params.get("username").toString();
        var password = params.get("password").toString();
        var newUser = new Param<>(new User());

        newUser.addOrderBy("id", SortType.ASC).queryObject.username = username;

        AuthUser user = userService.get(newUser);
        if (user != null) {
            return Json.ok("userAlreadyExists");
        } else {
            newUser.queryObject.isAdmin = false;
            newUser.queryObject.password = password;
            registeredUser(newUser.queryObject);
            return Json.ok("registerSuccess");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<String> getPerms(AuthUser user) {
        return getPermsByUser((User) user);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthUser getAuthUser(String username) {
        return userService.findByUsername(username);
    }

    /**
     * {@inheritDoc}
     *
     * @param params a {@link java.util.Map} object
     * @return a {@link cool.scx.auth.AuthUser} object
     * @throws cool.scx.exception.AuthException if any.
     */
    public AuthUser login(Map<String, Object> params) throws AuthException {
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
            var user = (User) userService.findByUsername(username.toString());
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

    /**
     * {@inheritDoc}
     *
     * @param user a {@link cool.scx._core.user.User} object.
     * @return a {@link cool.scx.auth.AuthUser} object.
     */
    public AuthUser registeredUser(User user) {
        var deptIds = user.deptIds;
        var roleIds = user.roleIds;
        var passwordAndSalt = encryptPassword(user.password);
        var coreUser = new User();
        coreUser.password = passwordAndSalt[0];
        coreUser.salt = passwordAndSalt[1];
        var newUser = userService.save(coreUser);
        deptService.saveDeptListWithUserId(newUser.id, deptIds);
        roleService.saveRoleListWithUserId(newUser.id, roleIds);
        return newUser;
    }

    /**
     * <p>encryptPassword.</p>
     *
     * @param password a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] encryptPassword(String password) {
        var passwordAndSalt = new String[2];
        var salt = StringUtils.getUUID().replace("-", "").substring(16);
        passwordAndSalt[1] = salt;
        try {
            String decrypt = CryptoUtils.encryptPassword(password, salt);
            passwordAndSalt[0] = decrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passwordAndSalt;
    }

    /**
     * 更新用户密码
     *
     * @param newUser a {@link cool.scx._core.user.User} object
     * @return a {@link cool.scx._core.user.User} object
     */
    public User updateUserPassword(User newUser) {
        var user = new User();
        if (!StringUtils.isEmpty(newUser.password)) {
            var passwordAndSalt = encryptPassword(newUser.password);
            user.password = passwordAndSalt[0];
            user.salt = passwordAndSalt[1];
        } else {
            user.password = null;
        }
        return userService.update(user);
    }

    /**
     * 根据用户获取权限字符串 这里不使用 list 而是 set 是为了去重
     *
     * @param user a {@link cool.scx.auth.AuthUser} object.
     * @return a {@link java.util.HashSet} object.
     */
    private HashSet<String> getPermsByUser(User user) {
        var permList = new HashSet<String>();
        //如果是超级管理员或管理员 直接设置为 *
        if (user.isAdmin) {
            permList.add("*");
        } else {
            roleService.getRoleListByUser(user).forEach(role -> permList.addAll(role.perms));
            deptService.getDeptListByUser(user).forEach(dept -> permList.addAll(dept.perms));
            //这里无论 是否有权限 都要给一个最基本的首页权限 不然用户进不去首页
            permList.add("/dashboard");
        }
        return permList;
    }

}
