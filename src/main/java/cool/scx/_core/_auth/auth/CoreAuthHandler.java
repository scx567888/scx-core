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
import cool.scx.auth.ScxAuth;
import cool.scx.auth.exception.UnknownDeviceException;
import cool.scx.auth.exception.UnknownUserException;
import cool.scx.auth.exception.WrongPasswordException;
import cool.scx.bo.QueryParam;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.DeviceType;
import cool.scx.enumeration.OrderByType;
import cool.scx.enumeration.WhereType;
import cool.scx.exception.AuthException;
import cool.scx.exception.UnauthorizedException;
import cool.scx.util.Ansi;
import cool.scx.util.CryptoUtils;
import cool.scx.util.NetUtils;
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
     * c
     *
     * @param licenseService a
     * @param userService    a
     * @param roleService    a
     * @param deptService    a
     */
    public CoreAuthHandler(LicenseService licenseService, UserService userService, RoleService roleService, DeptService deptService) {
        this.licenseService = licenseService;
        this.userService = userService;
        this.roleService = roleService;
        this.deptService = deptService;
    }

    /**
     * 获取用户信息
     *
     * @return a {@link cool.scx.vo.Json} object
     * @throws cool.scx.exception.UnauthorizedException if any.
     */
    public Json info() throws UnauthorizedException {
        var user = (User) ScxAuth.getLoginUser();
        //从session取出用户信息
        if (user == null) {
            throw new UnauthorizedException();
        } else {
            //返回登录用户的信息给前台 含用户的所有角色和权限
            return Json.ok()
                    .put("id", user.id)
                    .put("username", user.username)
                    .put("nickname", user.nickname)
                    .put("avatar", user.avatar)
                    .put("perms", getPerms(user))
                    .put("realDelete", ScxConfig.realDelete());
        }
    }

    /**
     * 更新用户信息
     *
     * @param newUserInfo a
     * @return a
     */
    public Json infoUpdate(User newUserInfo) {
        var currentUser = (User) ScxAuth.getLoginUser();
        //对密码进行特殊处理
        currentUser.password = StringUtils.isEmpty(newUserInfo.password) ? null : CryptoUtils.encryptPassword(newUserInfo.password);
        //更新成功
        if (userService.update(currentUser) != null) {
            Ansi.OUT.print("更新了自己的信息 用户名是 :" + currentUser.username).ln();
            return Json.ok();
        } else {
            return Json.fail();
        }
    }

    /**
     * logout
     *
     * @return a {@link cool.scx.vo.Json} object
     */
    public Json logout() {
        var ctx = ScxContext.routingContext();
        var b = ScxAuth.removeAuthUser(ctx);
        Ansi.OUT.print("当前总登录用户数量 : " + ScxAuth.getAllLoginItem().size() + " 个").ln();
        return b ? Json.ok() : Json.fail();
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
            return Json.fail("tooManyErrors").put("remainingTime", ((TooManyErrorsException) e).remainingTime);
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
        var queryParam = new QueryParam()
                .addOrderBy("id", OrderByType.ASC)
                .addWhere("username", WhereType.EQUAL, username);
        var user = userService.get(queryParam);
        if (user != null) {
            return Json.fail("userAlreadyExists");
        } else {
            var newUser = new User();
            newUser.isAdmin = false;
            newUser.password = password;
            registeredUser(newUser);
            return Json.fail("registerSuccess");
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
            var user = (User) userService.findByUsername(username);
            if (user == null) {
                var le = new LoginError(now, loginError.errorTimes + 1);
                loginErrorMap.put(ip, le);
                throw new UnknownUserException();
            }
            if (!CryptoUtils.checkPassword(password, user.password)) {
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
        user.password = CryptoUtils.encryptPassword(user.password);
        var newUser = userService.save(user);
        deptService.saveDeptListWithUserId(newUser.id, user.deptIds);
        roleService.saveRoleListWithUserId(newUser.id, user.roleIds);
        return newUser;
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
                return Json.fail("licenseError");
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
            Ansi.OUT.print(loginUser.username + " 登录了 , 登录设备 [" + loginDevice + "] , 当前总登录用户数量 : " + ScxAuth.getAllLoginItem().size() + " 个").ln();
            if (loginDevice == DeviceType.WEBSITE) {
                return Json.fail("login-successful");
            } else {
                return Json.ok().put("token", token);
            }
        } catch (AuthException authException) {
            return authExceptionHandler(authException);
        }
    }

}
