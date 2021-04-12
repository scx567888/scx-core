package cool.scx.core.user;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.FromPath;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.core.dept.Dept;
import cool.scx.core.dept.DeptService;
import cool.scx.core.dept.UserDept;
import cool.scx.core.dept.UserDeptService;
import cool.scx.core.license.LicenseService;
import cool.scx.core.role.RoleService;
import cool.scx.core.role.UserRole;
import cool.scx.core.role.UserRoleService;
import cool.scx.core.system.ScxLog;
import cool.scx.core.system.ScxLogService;
import cool.scx.enumeration.Device;
import cool.scx.enumeration.Method;
import cool.scx.enumeration.SortType;
import cool.scx.exception.AuthException;
import cool.scx.exception.TooManyErrorsException;
import cool.scx.exception.UnknownUserException;
import cool.scx.exception.WrongPasswordException;
import cool.scx.util.LogUtils;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>UserController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController("api/user")
public class UserController {

    private final UserService userService;
    private final ScxLogService scxLogService;
    private final DeptService deptService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final UserDeptService userDeptService;
    private final LicenseService licenseService;

    /**
     * <p>Constructor for UserController.</p>
     *
     * @param userService     a {@link cool.scx.core.user.UserService} object.
     * @param scxLogService   a {@link cool.scx.core.system.ScxLogService} object.
     * @param deptService     a {@link cool.scx.core.dept.DeptService} object.
     * @param roleService     a {@link cool.scx.core.role.RoleService} object.
     * @param userRoleService a {@link cool.scx.core.role.UserRoleService} object.
     * @param userDeptService a {@link cool.scx.core.dept.UserDeptService} object.
     * @param licenseService  r
     */
    public UserController(UserService userService, ScxLogService scxLogService, DeptService deptService, RoleService roleService, UserRoleService userRoleService, UserDeptService userDeptService, LicenseService licenseService) {
        this.userService = userService;
        this.scxLogService = scxLogService;
        this.deptService = deptService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.userDeptService = userDeptService;
        this.licenseService = licenseService;
    }

    /**
     * 登录方法
     * 此处有一个限制 若数据库中没有任何用户 为了防止
     * 系统无法登录 此处新建一个用户 名为 admin 密码为 password 的超级管理员用户
     *
     * @param username 用户 包含用户名和密码
     * @param password 密码
     * @return json
     */
    @ScxMapping(method = Method.POST)
    public Json login(@FromBody("username") String username, @FromBody("password") String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return Json.fail(StringUtils.isEmpty(username) ? "用户名不能为空" : "密码不能为空");
        }
        if (!licenseService.passLicense()) {
            return Json.fail(Json.FAIL_CODE, "licenseError");
        }
        try {
            var device = ScxContext.device();
            //登录
            var loginUser = userService.login(username, password);
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

    /**
     * <p>info.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = "info", method = Method.GET)
    public Json info() {
        var user = ScxContext.getLoginUser();
        //从session取出用户信息
        if (user == null) {
            return Json.fail(Json.ILLEGAL_TOKEN, "登录已失效");
        } else {
            //返回登录用户的信息给前台 含用户的所有角色和权限
            var permList = userService.getPermStrByUser(user);
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
     * 用户批量删除方法
     *
     * @param params model 的名称
     * @return json bool类型 true 代表删除成功 false 代表删除失败
     */
    @ScxMapping
    @SuppressWarnings("unchecked")
    public Json batchDelete(Map<String, Object> params) {
        var deleteIds = (ArrayList<Long>) params.get("deleteIds");
        for (Long delId : deleteIds) {
            userService.deleteUser(delId);
        }
        return Json.ok("success").data("deletedCount", deleteIds.size());
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
        var newUser = new Param<>(new User());

        newUser.addOrderBy("id", SortType.ASC).queryObject.username = username;

        User user = userService.get(newUser);
        if (user != null) {
            return Json.ok("userAlreadyExists");
        } else {
            newUser.queryObject.level = 4;
            newUser.queryObject.password = password;
            userService.registeredUser(newUser.queryObject);
            return Json.ok("registerSuccess");
        }
    }


    /**
     * <p>getUserById.</p>
     *
     * @param id a {@link java.lang.Long} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = ":id", method = Method.GET)
    public Json getUserById(@FromPath Long id) {
        if (StringUtils.isNotEmpty(id)) {
            var user = userService.getById(id);
            if (user != null) {
                return Json.ok().data("user", user).data("userDeptList", deptService.findDeptByUserId(id)).data("userRoleList", roleService.findRoleByUserId(id));
            }
        }
        return Json.ok().data("user", null);
    }


    /**
     * <p>addUser.</p>
     *
     * @param user a {@link cool.scx.core.user.User} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping
    public Json addUser(User user) {
        if (user.username != null && user.password != null) {
            return Json.ok().items(userService.registeredUser(user));
        } else {
            return Json.fail("");
        }
    }

    /**
     * <p>updateUser.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = false, method = Method.PUT)
    public Json updateUser(Map<String, Object> params) {
        var user = ObjectUtils.mapToBean(params, User.class);
        Objects.requireNonNull(user).username = null;
        var updateUser = userService.updateUser(user);
        return Json.ok().items(updateUser);
    }


    /**
     * 退出登录方法 清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(method = Method.POST)
    public Json logout() {
        ScxContext.removeLoginUser();
        return Json.ok("User Logged Out");
    }

    /**
     * 根据用户名查找用户
     *
     * @param queryUser 查询的用户
     * @return 是否查找到
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json findByUsername(User queryUser) {
        var user = userService.findByUsername(queryUser.username);
        if (user == null) {
            return Json.ok().data("success", true);
        } else {
            return Json.ok().data("success", false);
        }
    }

    /**
     * <p>avatarUpdate.</p>
     *
     * @param queryUser a {@link cool.scx.core.user.User} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json avatarUpdate(User queryUser) {
        var currentUser = ScxContext.getLoginUser();
        currentUser.avatar = queryUser.avatar;
        var b = userService.update(currentUser) != null;
        LogUtils.recordLog("更改了头像 用户名是 :" + currentUser.username);
        return Json.ok().data("success", b);
    }

    /**
     * <p>getUserLog.</p>
     *
     * @param context a  object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json getUserLog(RoutingContext context) {
        var currentUser = ScxContext.getLoginUser();
        var scxLog = new Param<>(new ScxLog());
        scxLog.queryObject.username = currentUser.username;
        scxLog.queryObject.type = 1;
        scxLog.setPagination(1, 6).addOrderBy("id", SortType.DESC);
        var byCondition = scxLogService.list(scxLog);
        return Json.ok().items(byCondition);
    }

    /**
     * 用户自己更新的信息
     *
     * @param params 用户信息
     * @return 通知
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json infoUpdate(Map<String, Object> params) {
        var queryUser = ObjectUtils.mapToBean(params, User.class);
        var currentUser = ScxContext.getLoginUser();
        currentUser.nickName = queryUser.nickName;
        currentUser.phone = queryUser.phone;
        currentUser.password = queryUser.password;
        currentUser.salt = null;
        var b = userService.updateUserPassword(currentUser) != null;
        LogUtils.recordLog("更新了自己的信息", "用户名是 :" + currentUser.username);
        return Json.ok().data("success", b);
    }


    /**
     * 删除恢复方法
     *
     * @param id id
     * @return json
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json revokeDelete(Long id) {
        return Json.ok(userService.revokeDeleteUser(id) != null ? "success" : "error");
    }

    /**
     * <p>listUser.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json listUser(Map<String, Object> params) {
        var user = new Param<>(ObjectUtils.mapToBean(params, User.class));
        var deptIds = params.get("deptIds");
        var roleIds = params.get("roleIds");
        var userIdsByDept = "";
        var userIdsByRole = "";
        if (deptIds != null) {
            var userDept = new Param<>(new UserDept());
            userDept.whereSql = "dept_id in (" + deptIds + ")";
            userIdsByDept = userDeptService.list(userDept).stream().map(item -> item.userId.toString()).distinct().collect(Collectors.joining(",", "", ""));
        }
        if (roleIds != null) {
            var userRole = new Param<>(new UserRole());
            userRole.whereSql = "role_id in (" + roleIds + ")";
            userIdsByRole = userRoleService.list(userRole).stream().map(item -> item.userId.toString()).distinct().collect(Collectors.joining(",", "", ""));
        }

        if (deptIds != null || roleIds != null) {
            if (StringUtils.isNotEmpty(userIdsByDept) && StringUtils.isEmpty(userIdsByRole)) {
                user.whereSql = " id in (" + userIdsByDept + ")";
            }
            if (StringUtils.isNotEmpty(userIdsByRole) && StringUtils.isEmpty(userIdsByDept)) {
                user.whereSql = " id in (" + userIdsByRole + ")";
            }
            if (StringUtils.isNotEmpty(userIdsByDept) && StringUtils.isNotEmpty(userIdsByRole)) {
                user.whereSql = " id in (" + userIdsByRole + ") AND id in (" + userIdsByDept + ")";
            }
            if (StringUtils.isEmpty(userIdsByDept) && StringUtils.isEmpty(userIdsByRole)) {
                user.whereSql = " id in (-1)";
            }
        }

        var list = userService.list(user);
        var count = userService.count(user);
        if (list.size() == 0) {
            return Json.ok().tables(new String[]{}, 0);
        }
        return Json.ok().tables(list, count);
    }

    /**
     * <p>listSubUser.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json listSubUser() {
        var userAndDeptTree = new ArrayList<HashMap<String, Object>>();
        var queryUser = new Param<>(new User());
        queryUser.queryObject.level = 4;
        queryUser.addOrderBy("personOrder", SortType.DESC);
        var deptList = deptService.listAll();
        for (User user : userService.list(queryUser)) {
            var tempUserMap = new HashMap<String, Object>();
            tempUserMap.put("id", "u_" + user.id);
            tempUserMap.put("label", user.nickName);
            String[] split = user.deptIds.split(",");
            for (String s : split) {
                List<Dept> collect = deptList.stream().filter(dept -> dept.id.toString().equals(s)).collect(Collectors.toList());
                if (collect.size() > 0) {
                    Dept dept = collect.get(0);
                    if (dept == null) {
                        tempUserMap.put("parentId", 0);
                    } else {
                        tempUserMap.put("parentId", dept.id);
                    }
                    userAndDeptTree.add(tempUserMap);
                }
            }
        }
        var p = new Param<>(new Dept());
        p.addOrderBy("level", SortType.DESC).addOrderBy("deptOrder", SortType.DESC);
        for (Dept dept : deptService.list(p)) {
            var tempDeptMap = new HashMap<String, Object>();
            tempDeptMap.put("id", dept.id);
            tempDeptMap.put("label", dept.deptName);
            tempDeptMap.put("parentId", dept.parentId);
            userAndDeptTree.add(tempDeptMap);
        }
        return Json.ok().items(userAndDeptTree);
    }

}
