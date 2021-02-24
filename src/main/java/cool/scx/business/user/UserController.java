package cool.scx.business.user;

import cool.scx.annotation.BodyParam;
import cool.scx.annotation.PathParam;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.Param;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.business.dept.Dept;
import cool.scx.business.dept.DeptService;
import cool.scx.business.dept.UserDept;
import cool.scx.business.dept.UserDeptService;
import cool.scx.business.role.RoleService;
import cool.scx.business.role.UserRole;
import cool.scx.business.role.UserRoleService;
import cool.scx.business.system.ScxLog;
import cool.scx.business.system.ScxLogService;
import cool.scx.enumeration.CheckLoginType;
import cool.scx.enumeration.RequestMethod;
import cool.scx.enumeration.SortType;
import cool.scx.exception.AuthException;
import cool.scx.exception.TooManyErrorsException;
import cool.scx.exception.UnknownUserException;
import cool.scx.exception.WrongPasswordException;
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

    /**
     * <p>Constructor for UserController.</p>
     *
     * @param userService     a {@link cool.scx.business.user.UserService} object.
     * @param scxLogService   a {@link cool.scx.business.system.ScxLogService} object.
     * @param deptService     a {@link cool.scx.business.dept.DeptService} object.
     * @param roleService     a {@link cool.scx.business.role.RoleService} object.
     * @param userRoleService a {@link cool.scx.business.role.UserRoleService} object.
     * @param userDeptService a {@link cool.scx.business.dept.UserDeptService} object.
     */
    public UserController(UserService userService, ScxLogService scxLogService, DeptService deptService, RoleService roleService, UserRoleService userRoleService, UserDeptService userDeptService) {
        this.userService = userService;
        this.scxLogService = scxLogService;
        this.deptService = deptService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.userDeptService = userDeptService;
    }

    /**
     * 登录方法
     * 此处有一个限制 若数据库中没有任何用户 为了防止
     * 系统无法登录 此处新建一个用户 名为 admin 密码为 password 的超级管理员用户
     *
     * @param username 用户 包含用户名和密码
     * @param password 密码
     * @param ctx      上下文
     * @return json
     */
    @ScxMapping(checkedLogin = CheckLoginType.None)
    public Json login(@BodyParam("username") String username, @BodyParam("password") String password, RoutingContext ctx) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return Json.fail(StringUtils.isEmpty(username) ? "用户名不能为空" : "密码不能为空");
        }
        try {
            //登录
            var loginUser = userService.login(username, password, ctx);
            var token = StringUtils.getUUID();
            ScxContext.addUserToSession(token, loginUser.username);
            //返回登录用户的 Token 给前台，角色和权限信息通过 auth/info 获取
            return Json.ok().data("token", token);
        } catch (UnknownUserException uue) {
            return Json.fail(ScxConfig.confusionLoginError ? "usernameOrPasswordError" : "userNotFound");
        } catch (WrongPasswordException wpe) {
            //这里和用户密码错误   可以使用相同的 提示信息 防止恶意破解
            return Json.fail(ScxConfig.confusionLoginError ? "usernameOrPasswordError" : "passwordError");
        } catch (TooManyErrorsException tee) {
            //密码错误次数过多
            return Json.fail("tooManyErrors").data("remainingTime", tee.remainingTime);
        } catch (AuthException ae) {
            ScxLogService.outLog("登录出错 : " + ae.getMessage(), true);
            return Json.fail("logonFailure");
        } catch (Exception e) {
            ScxLogService.outLog("密码加密校验出错 : " + e.getMessage(), true);
            return Json.fail("logonFailure");
        }
    }

    /**
     * <p>info.</p>
     *
     * @param token a  object.
     * @return a {@link cool.scx.vo.Json} object.
     */

    @ScxMapping(value = "info/:token", method = RequestMethod.GET)
    public Json info(@PathParam String token) {
        var userId = ScxContext.getCurrentUserByToken(token);
        //从session取出用户信息
        if (userId == null) {
            return Json.fail(Json.SESSION_TIMEOUT, "登录已失效");
        } else {
            //每次都从数据库中获取用户
            var user = userService.getById(userId.id);
            //返回登录用户的信息给前台 含用户的所有角色和权限
            var permList = userService.getPermStrByUser(user);
            //这里无论 是否有权限 都要给一个最基本的首页权限
            List<String> perms = new ArrayList<>();
            if (permList == null || permList.size() == 0) {
                perms.add("/dashboard");
            } else {
                perms = permList;
            }
            return Json.ok()
                    .data("userId", user.id)
                    .data("name", user.username)
                    .data("nickName", user.nickName)
                    .data("avatarId", user.avatarId)
                    .data("perms", perms);
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
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping
    public Json register(Map<String, Object> params) {
        var username = (String) params.get("username");
        var password = (String) params.get("password");
        var newUser = new Param<>(new User());

        newUser.addOrderBy("id", SortType.ASC).queryObject.username = username;

        User user = userService.get(newUser);
        if (user != null) {
            return Json.ok("userAlreadyExists");
        } else {
            newUser.queryObject.level = 4;
            newUser.queryObject.level = 0;
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
    @ScxMapping(value = ":id", method = RequestMethod.GET)
    public Json getUserById(Long id) {
        if (StringUtils.isNotEmpty(id)) {
            var user = userService.getById(id);
            if (user != null) {
                return Json.ok().data("success", true).data("user", user).data("userDeptList", deptService.findDeptByUserId(id)).data("userRoleList", roleService.findRoleByUserId(id));
            }
        }
        return Json.ok().data("success", false);
    }


    /**
     * <p>addUser.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping
    public Json addUser(Map<String, Object> params) {
        var user = ObjectUtils.mapToBean(params, User.class);
        if (user.username != null && user.password != null) {
            user.id = userService.registeredUser(user);
        }
        return Json.ok().items(user);
    }

    /**
     * <p>updateUser.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json updateUser(Map<String, Object> params) {
        var user = ObjectUtils.mapToBean(params, User.class);
        Objects.requireNonNull(user).username = null;
        var updateUser = userService.updateUser(user);
        return Json.ok().items(updateUser);
    }


    /**
     * 退出登录方法 清空 session 里的登录数据
     *
     * @param ctx RoutingContext
     * @return 是否成功退出
     */
    @ScxMapping
    public Json logout(RoutingContext ctx) {
        ScxContext.logoutUserByHeader(ctx);
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
     * @param queryUser a {@link cool.scx.business.user.User} object.
     * @param context   a  object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json avatarUpdate(User queryUser, RoutingContext context) {
        var currentUser = ScxContext.getCurrentUserByHeader(context);
        currentUser.avatarId = queryUser.avatarId;
        var b = userService.update(currentUser) != null;
        ScxLogService.outLog("更改了头像 用户名是 :" + currentUser.username);
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
        var currentUser = ScxContext.getCurrentUserByHeader(context);
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
     * @param params  用户信息
     * @param context 上下问
     * @return 通知
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json infoUpdate(Map<String, Object> params, RoutingContext context) {
        var queryUser = ObjectUtils.mapToBean(params, User.class);
        var currentUser = ScxContext.getCurrentUserByHeader(context);
        currentUser.nickName = queryUser.nickName;
        currentUser.phone = queryUser.phone;
        currentUser.password = queryUser.password;
        currentUser.salt = null;
        var b = userService.updateUserPassword(currentUser) != null;
        scxLogService.recordLog("更新了自己的信息", "用户名是 :" + currentUser.username, context);
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
