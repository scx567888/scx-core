package cool.scx._core._auth.auth;

import cool.scx._core._auth.AuthConfig;
import cool.scx._core._auth.AuthModuleOption;
import cool.scx._core._auth.dept.DeptService;
import cool.scx._core._auth.license.LicenseService;
import cool.scx._core._auth.role.RoleService;
import cool.scx._core._auth.user.User;
import cool.scx._core._auth.user.UserService;
import cool.scx.annotation.ScxService;
import cool.scx.auth.AuthHandler;
import cool.scx.auth.AuthUser;
import cool.scx.auth.AuthUtils;
import cool.scx.auth.ScxAuth;
import cool.scx.auth.exception.UnknownDeviceException;
import cool.scx.auth.exception.UnknownUserException;
import cool.scx.auth.exception.WrongPasswordException;
import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.enumeration.SortType;
import cool.scx.exception.AuthException;
import cool.scx.util.Ansi;
import cool.scx.util.NetUtils;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心包的权限处理器
 * 如有需求可以自行实现 AuthHandler 接口
 *
 * @author 司昌旭
 * @version 1.0.10
 */
@ScxService
public class CoreAuthHandler implements AuthHandler {

    private static final Map<String, LoginError> loginErrorMap = new ConcurrentHashMap<>();
    private final LicenseService licenseService;
    private final UserService userService;
    private final RoleService roleService;
    private final DeptService deptService;

    /**
     * <p>Constructor for CoreAuthHandler.</p>
     *
     * @param licenseService a
     * @param userService    a {@link cool.scx._core._auth.user.UserService} object
     * @param roleService    a {@link cool.scx._core._auth.role.RoleService} object
     * @param deptService    a {@link cool.scx._core._auth.dept.DeptService} object
     */
    public CoreAuthHandler(LicenseService licenseService, UserService userService, RoleService roleService, DeptService deptService) {
        this.licenseService = licenseService;
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
            return Json.ok()
                    .data("id", user.id)
                    .data("username", user.username)
                    .data("nickName", user.nickName)
                    .data("avatar", user.avatar)
                    .data("perms", getPerms(user))
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
        Ansi.OUT.print("更新了自己的信息 用户名是 :" + currentUser.username).ln();
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
        } else if (e instanceof UnknownUserException) {
            return Json.fail(AuthConfig.confusionLoginError() ? "usernameOrPasswordError" : "userNotFound");
        } else if (e instanceof WrongPasswordException) {
            //这里和用户密码错误   可以使用相同的 提示信息 防止恶意破解
            return Json.fail(AuthConfig.confusionLoginError() ? "usernameOrPasswordError" : "passwordError");
        } else if (e instanceof TooManyErrorsException) {
            //密码错误次数过多
            return Json.fail("tooManyErrors").data("remainingTime", ((TooManyErrorsException) e).remainingTime);
        } else {
            Ansi.OUT.print("登录出错 : " + e.getMessage()).ln();
            return Json.fail("logonFailure");
        }
    }

    /**
     * signup
     *
     * @param username a {@link java.util.Map} object
     * @param password a {@link java.util.Map} object
     * @return a {@link cool.scx.vo.Json} object
     */
    public Json signup(String username, String password) {
        var newUser = new Param<>(new User());
        newUser.addOrderBy("id", SortType.ASC).queryObject.username = username;
        User user = userService.get(newUser);
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
        var permList = new HashSet<String>();
        //如果是超级管理员或管理员 直接设置为 *
        if (user._IsAdmin()) {
            permList.add("*");
        } else {
            roleService.getRoleListByUser((User) user).forEach(role -> permList.addAll(role.perms));
            deptService.getDeptListByUser((User) user).forEach(dept -> permList.addAll(dept.perms));
        }
        return permList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthUser getAuthUser(String uniqueID) {
        return userService.findByUsername(uniqueID);
    }

    /**
     * {@inheritDoc}
     *
     * @param username 用户名
     * @param password 密码
     * @return a {@link cool.scx.auth.AuthUser} object
     * @throws cool.scx.exception.AuthException if any.
     */
    private User tryLogin(String username, String password) throws AuthException {
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
            if (!AuthUtils.verifyPassword(user.password, user.salt, password.toString())) {
                var le = new LoginError(now, loginError.errorTimes + 1);
                loginErrorMap.put(ip, le);
                throw new WrongPasswordException();
            }
            return user;
        } else {
            Ansi.OUT.print(ip + " : 错误登录次数过多").ln();
            var duration = Duration.between(now, loginError.lastErrorDate).toSeconds();
            throw new TooManyErrorsException(duration);
        }
    }

    private boolean notHaveLoginError(String ip, LoginError loginError) {
        if (LocalDateTime.now().isBefore(loginError.lastErrorDate)) {
            return false;
        } else if (loginError.errorTimes >= AuthConfig.loginErrorLockTimes()) {
            LoginError le = new LoginError(LocalDateTime.now().plusSeconds(AuthConfig.loginErrorLockSecond()), 0);
            loginErrorMap.put(ip, le);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param user a {@link cool.scx._core._auth.user.User} object.
     * @return a {@link cool.scx.auth.AuthUser} object.
     */
    public User registeredUser(User user) {
        var deptIds = user.deptIds;
        var roleIds = user.roleIds;
        var passwordAndSalt = AuthUtils.getPasswordAndSalt(user.password);
        var coreUser = new User();
        coreUser.password = passwordAndSalt[0];
        coreUser.salt = passwordAndSalt[1];
        var newUser = userService.save(coreUser);
        deptService.saveDeptListWithUserId(newUser.id, deptIds);
        roleService.saveRoleListWithUserId(newUser.id, roleIds);
        return newUser;
    }

    /**
     * 更新用户密码
     *
     * @param newUser a {@link cool.scx._core._auth.user.User} object
     * @return a {@link cool.scx._core._auth.user.User} object
     */
    public User updateUserPassword(User newUser) {
        var user = new User();
        if (!StringUtils.isEmpty(newUser.password)) {
            var passwordAndSalt = AuthUtils.getPasswordAndSalt(newUser.password);
            user.password = passwordAndSalt[0];
            user.salt = passwordAndSalt[1];
        } else {
            user.password = null;
        }
        return userService.update(user);
    }

    /**
     * 登录
     *
     * @param username u
     * @param password p
     * @param ctx      c
     * @return j
     */
    public Json login(String username, String password, RoutingContext ctx) {
        try {
            if (AuthModuleOption.loginUseLicense() && !licenseService.passLicense()) {
                return Json.fail(Json.FAIL_CODE, "licenseError");
            }
            if (StringUtils.isEmpty(username)) {
                return Json.fail("用户名不能为空");
            } else if (StringUtils.isEmpty(password)) {
                return Json.fail("密码不能为空");
            }
            var loginUser = tryLogin(username, password);
            var token = ScxAuth.addAuthUser(ctx, loginUser);
            //这里根据登录设备向客户端返回不同的信息
            var loginDevice = ScxAuth.getDevice(ScxContext.routingContext());
            if (loginDevice == Device.WEBSITE) {
                return Json.ok("login-successful");
            } else {
                return Json.ok().data("token", token);
            }
        } catch (AuthException authException) {
            return authExceptionHandler(authException);
        }
    }

}
