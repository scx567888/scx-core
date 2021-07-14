package cool.scx._ext.organization;

import cool.scx.annotation.ScxService;
import cool.scx.auth.AuthHandler;
import cool.scx.auth.AuthUser;
import cool.scx.auth.ScxAuth;
import cool.scx.auth.exception.UnknownDeviceException;
import cool.scx.auth.exception.UnknownUserException;
import cool.scx.auth.exception.WrongPasswordException;
import cool.scx.bo.Query;
import cool.scx.bo.Where;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.DeviceType;
import cool.scx.exception.AuthException;
import cool.scx.exception.UnauthorizedException;
import cool.scx.util.Ansi;
import cool.scx.util.CryptoUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * 核心包的权限处理器
 * 如有需求可以自行实现 AuthHandler 接口
 *
 * @author scx567888
 * @version 1.0.10
 */
@ScxService
public class OrganizationAuthHandler implements AuthHandler {

    private final UserService userService;
    private final RoleService roleService;
    private final DeptService deptService;

    /**
     * c
     *
     * @param userService a
     * @param roleService a
     * @param deptService a
     */
    public OrganizationAuthHandler(UserService userService, RoleService roleService, DeptService deptService) {
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
     * 认证异常处理器
     *
     * @param e a {@link cool.scx.exception.AuthException} object
     * @return a {@link cool.scx.vo.Json} object
     */
    private Json authExceptionHandler(AuthException e) {
        if (e instanceof UnknownDeviceException) {
            return Json.fail("未知设备");
        } else if (e instanceof UnknownUserException) {
            return Json.fail(OrganizationConfig.confusionLoginError() ? "usernameOrPasswordError" : "userNotFound");
        } else if (e instanceof WrongPasswordException) {
            //这里和用户密码错误   可以使用相同的 提示信息 防止恶意破解
            return Json.fail(OrganizationConfig.confusionLoginError() ? "usernameOrPasswordError" : "passwordError");
        } else {
            Ansi.OUT.print("登录出错 : " + e.getMessage()).ln();
            return Json.fail("logonFailure");
        }
    }

    /**
     * 注册一个用户
     *
     * @param username a {@link java.util.Map} object
     * @param password a {@link java.util.Map} object
     * @return a {@link cool.scx.vo.Json} object
     */
    public Json signup(String username, String password) {
        //判断用户是否存在
        if (userService.get(new Query().equal("username", username)) != null) {
            return Json.fail("userAlreadyExists");
        }
        var user = new User();
        user.username = username;
        user.password = CryptoUtils.encryptPassword(password);
        user.isAdmin = false;
        return userService.save(user) != null ? Json.ok() : Json.fail("signup-error");
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
        return userService.get(new Query().equal("username", uniqueID));
    }

    /**
     * 尝试登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户
     * @throws cool.scx.exception.AuthException 登录失败的错误
     */
    private User tryLogin(String username, String password) throws AuthException {
        var user = userService.get(new Query().equal("username", username));
        if (user == null) {
            throw new UnknownUserException();
        }
        if (!CryptoUtils.checkPassword(password, user.password)) {
            throw new WrongPasswordException();
        }
        return user;
    }

    /**
     * 登录
     *
     * @param username u
     * @param password p
     * @return j
     */
    public Json login(String username, String password) {
        try {
            //尝试登录
            var loginUser = tryLogin(username, password);
            //登录成功获取 token
            var token = ScxAuth.addAuthUser(ScxContext.routingContext(), loginUser);
            //更新用户的最后一次登录的 时间和ip
            updateLastLoginDateAndIP(loginUser.username);
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

    /**
     * 更新有关用户登录的信息
     *
     * @param username 用户名
     */
    private void updateLastLoginDateAndIP(String username) {
        var tempUser = new User();
        tempUser.lastLoginDate = LocalDateTime.now();
        tempUser.lastLoginIP = NetUtils.getIpAddr();
        userService.update(tempUser, new Where().equal("username", username));
    }

}
