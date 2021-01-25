package cool.scx.service.user;

import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.enumeration.HttpMethod;
import cool.scx.enumeration.SortType;
import cool.scx.service.license.LicenseService;
import cool.scx.service.role.Role;
import cool.scx.service.system.ScxLog;
import cool.scx.service.system.ScxLogService;
import cool.scx.service.user.exception.AuthException;
import cool.scx.service.user.exception.TooManyErrorsException;
import cool.scx.service.user.exception.UnknownUserException;
import cool.scx.service.user.exception.WrongPasswordException;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.service.dept.Dept;
import cool.scx.service.dept.DeptService;
import cool.scx.service.dept.UserDept;
import cool.scx.service.dept.UserDeptService;
import cool.scx.service.role.RoleService;
import cool.scx.service.role.UserRole;
import cool.scx.service.role.UserRoleService;
import cool.scx.util.CryptoUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@ScxService("api/userService")
public class UserService extends BaseService<User> {

    private static final HashMap<String, LoginError> loginErrorMap = new HashMap<>();
    private final UserDeptService userDeptService;
    private final UserRoleService userRoleService;
    private final DeptService deptService;
    private final RoleService roleService;
    private final LicenseService licenseService;
    private final ScxLogService scxLogService;

    public UserService(UserDeptService userDeptService, UserRoleService userRoleService, DeptService deptService, RoleService roleService, LicenseService licenseService, ScxLogService scxLogService) {
        this.userDeptService = userDeptService;
        this.userRoleService = userRoleService;
        this.deptService = deptService;
        this.roleService = roleService;
        this.licenseService = licenseService;
        this.scxLogService = scxLogService;
    }

    @Override
    @ScxMapping(useMethodNameAsUrl = true, httpMethod = HttpMethod.GET)
    public List<User> listAll() {
        var userDeptListFuture = CompletableFuture.supplyAsync(userDeptService::listAll);
        var userRoleListFuture = CompletableFuture.supplyAsync(userRoleService::listAll);
        var userListFuture = CompletableFuture.supplyAsync(super::listAll);
        try {
            var userDeptList = userDeptListFuture.get();
            var userRoleList = userRoleListFuture.get();
            return userListFuture.get().stream().peek(item -> {
                item.deptIds = userDeptList.stream().filter(userDept -> userDept.userId.equals(item.id)).map(deptItem -> deptItem.deptId.toString()).collect(Collectors.joining(",", "", ""));
                item.roleIds = userRoleList.stream().filter(userRole -> userRole.userId.equals(item.id)).map(deptItem -> deptItem.roleId.toString()).collect(Collectors.joining(",", "", ""));
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getCurrentUserPermsList() {
        var strings = checkPerm(ScxContext.getCurrentUser());
        var tmp = new StringBuilder();
        for (String string : strings) {
            tmp.append(string).append(",");
        }
        var split = tmp.toString().split(",");
        return Arrays.asList(split);
    }

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户
     * @return s
     */
    public List<String> checkPerm(User user) {
        var permList = new ArrayList<String>();
        //如果是超级管理员 直接设置为 *
        if (user.level == 2) {
            permList.add("*");
        } else {
            //不是超级管理员
            if (user.level != null) {
                var rank = user.level;

                List<Dept> deptList;
                List<Role> roleList;

                var deptParam = new Param<>(new Dept());

                var collect = userDeptService.getListByUser(user).stream().map(UserDept -> UserDept.deptId.toString()).collect(Collectors.joining(","));
                if (!"".equals(collect)) {
                    deptParam.whereSql = " id in (" + collect + ")";
                    deptList = deptService.list(deptParam);
                } else {
                    deptList = new ArrayList<>();
                }


                var roleParam = new Param<>(new Role());
                String collect1 = userRoleService.getListByUser(user).stream().map(UserRole -> UserRole.roleId.toString()).collect(Collectors.joining(","));
                if (!"".equals(collect1)) {
                    roleParam.whereSql = " id in (" + collect1 + ")";
                    roleList = roleService.list(roleParam);
                } else {
                    roleList = new ArrayList<>();
                }


                var stringStream = roleList.stream().filter(Role -> Role.perm != null && !Role.perm.equals("")).map(Role -> Role.perm).collect(Collectors.joining(";")).split(";");

                permList.addAll(Arrays.asList(stringStream));

                var objects = deptList.stream().filter(Dept -> Dept.perm != null && !Dept.perm.equals("")).map(Dept -> Dept.perm).toArray(String[]::new);
                for (String perm : objects) {
                    var permArr = perm.split(";");
                    for (String s : permArr) {
                        var arr = s.split("_");
                        if (arr.length > 1) {
                            var tempRank = 0;
                            try {
                                tempRank = Integer.parseInt(arr[1]);
                            } catch (Exception ignored) {

                            }
                            if (tempRank <= rank) {
                                permList.add(arr[0]);
                            }
                        }
                    }
                }
            }
        }
        return permList;
    }

    @ScxMapping(value = ":id", httpMethod = HttpMethod.GET)
    public Json getUserById(Long id) {
        if (StringUtils.isNotEmpty(id)) {
            var user = getById(id);
            if (user != null) {
                return Json.ok().data("success", true).data("user", user).data("userDeptList", findDeptByUserId(id)).data("userRoleList", findRoleByUserId(id));
            }
        }
        return Json.ok().data("success", false);
    }

    public List<UserDept> findDeptByUserId(Long userId) {

        if (StringUtils.isNotEmpty(userId)) {
            var ud = new Param<>(new UserDept());
            ud.queryObject.userId = userId;
            return userDeptService.list(ud);
        }
        return null;
    }

    public List<UserRole> findRoleByUserId(Long userId) {
        if (StringUtils.isNotEmpty(userId)) {
            var userRole = new Param<>(new UserRole());
            userRole.queryObject.userId = userId;
            return userRoleService.list(userRole);
        }
        return null;
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json addUser(Map<String, Object> params) {
        var user = ObjectUtils.mapToBean(params, User.class);
        if (user.username != null && user.password != null) {
            user.id = registeredUser(user);
        }
        return Json.ok().items(user);
    }

    /**
     * 注册用户
     *
     * @param user 新建用户
     * @return 新增的 id
     */
    public Long registeredUser(User user) {
        var deptIds = user.deptIds;
        var roleIds = user.roleIds;
        var passwordAndSalt = encryptPassword(user.password);
        user.password = passwordAndSalt[0];
        user.salt = passwordAndSalt[1];
        var tempUserId = save(user);
        saveUserDeptIds(tempUserId, deptIds);
        saveUserRoleIds(tempUserId, roleIds);
        return tempUserId;
    }

    /**
     * 加密密码 返回加密后的密码 和盐值
     * 用于新建用户
     */
    private String[] encryptPassword(String password) {
        var passwordAndSalt = new String[2];
        var uuid = StringUtils.getUUID();
        var salt = uuid.replace("-", "").substring(16);
        passwordAndSalt[1] = salt;
        try {
            String decrypt = CryptoUtils.encryptPassword(password, salt);
            passwordAndSalt[0] = decrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passwordAndSalt;
    }

    private void saveUserDeptIds(Long userId, String deptIds) {
        var userDeptList = new ArrayList<UserDept>();
        if (!StringUtils.isEmpty(deptIds)) {
            var idArr = deptIds.split(",");
            if (idArr.length > 0) {
                for (String s : idArr) {
                    if (!StringUtils.isEmpty(s)) {
                        var userDept = new UserDept();
                        userDept.userId = userId;
                        userDept.deptId = Long.parseLong(s);
                        userDeptList.add(userDept);
                    }
                }
                userDeptService.saveList(userDeptList);
            }
        }
    }

    private void saveUserRoleIds(Long userId, String roleIds) {
        var userRoleList = new ArrayList<UserRole>();
        if (!StringUtils.isEmpty(roleIds)) {
            var idArr = roleIds.split(",");
            if (idArr.length > 0) {
                for (String s : idArr) {
                    if (!StringUtils.isEmpty(s)) {
                        var userRole = new UserRole();
                        userRole.userId = userId;
                        userRole.roleId = Long.parseLong(s);
                        userRoleList.add(userRole);
                    }
                }
                userRoleService.saveList(userRoleList);
            }
        }
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json updateUser(Map<String, Object> params) {
        var user = ObjectUtils.mapToBean(params, User.class);
        Objects.requireNonNull(user).username = null;
        var updateUser = updateUser(user);
        return Json.ok().items(updateUser);
    }

    public User updateUser(User user) {
        var deptIds = user.deptIds;
        var roleIds = user.roleIds;
        if (!StringUtils.isEmpty(user.password)) {
            var passwordAndSalt = encryptPassword(user.password);
            user.password = passwordAndSalt[0];
            user.salt = passwordAndSalt[1];
        } else {
            user.password = null;
        }

        var deleteUserDept = new Param<>(new UserDept());
        deleteUserDept.queryObject.userId = user.id;
        userDeptService.delete(deleteUserDept);

        var deleteUserRole = new Param<>(new UserRole());
        deleteUserRole.queryObject.userId = user.id;
        userRoleService.delete(deleteUserRole);
        saveUserDeptIds(user.id, deptIds);
        saveUserRoleIds(user.id, roleIds);
        User update = updateById(user);
        update.roleIds = user.roleIds;
        update.deptIds = user.deptIds;
        return update;
    }

    /**
     * 登录方法
     * 此处有一个限制 若数据库中没有任何用户 为了防止
     * 系统无法登录 此处新建一个用户 名为 admin 密码为 password 的超级管理员用户
     *
     * @param username 用户 包含用户名和密码
     * @return json
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json login(String username, String password) {
        if (StringUtils.isEmpty(username)) {
            return Json.fail("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            return Json.fail("密码不能为空");
        }
        var licenseRight = licenseService.passLicense();

        if (!licenseRight) {
            return Json.fail(Json.FAIL_CODE, "licenseError");
        }

        try {
            //登录
            var loginUser = dologin(username, password);
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

    public User dologin(String username, String password) throws AuthException {
        var now = LocalDateTime.now();
        var ip = NetUtils.getIpAddr();
        var loginError = loginErrorMap.get(ip);
        if (loginError == null) {
            var le = new LoginError();
            le.lastErrorDate = LocalDateTime.now();
            le.errorTimes = 0;
            loginErrorMap.put(ip, le);
            loginError = le;
        }
        if (notHaveLoginError(ip, loginError)) {
            var user = findByUsername(username);
            if (user == null) {
                LoginError le = new LoginError();
                le.lastErrorDate = now;
                le.errorTimes = loginError.errorTimes + 1;
                loginErrorMap.put(ip, le);
                throw new UnknownUserException();
            }
            if (!verifyPassword(user, password)) {
                LoginError le = new LoginError();
                le.lastErrorDate = now;
                le.errorTimes = loginError.errorTimes + 1;
                loginErrorMap.put(ip, le);
                throw new WrongPasswordException();
            }
            return user;
        } else {
            ScxLogService.outLog(ip + " : 错误登录次数过多");
            var duration = Duration.between(now, loginError.lastErrorDate).toSeconds();
            throw new TooManyErrorsException(duration);
        }
    }

    public User findByUsername(String username) {
        var param = new Param<>(new User());
        param.queryObject.username = username;
        return get(param);
    }

    private boolean notHaveLoginError(String ip, LoginError loginError) {
        if (LocalDateTime.now().isBefore(loginError.lastErrorDate)) {
            return false;
        } else if (loginError.errorTimes >= ScxConfig.loginErrorLockTimes) {
            LoginError le = new LoginError();
            le.lastErrorDate = LocalDateTime.now().plusSeconds(ScxConfig.loginErrorLockSecond);
            le.errorTimes = 0;
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
            var decryptPassword = decryptPassword(user.password, user.salt);
            return password.equals(decryptPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解密密码 返回加密后的密码 和盐值
     * 用于登录或找回密码
     */
    private String decryptPassword(String password, String salt) {
        try {
            return CryptoUtils.decryptPassword(password, salt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @ScxMapping(value = "info/:token", httpMethod = HttpMethod.GET)
    public Json info(String token) {
        var userId = ScxContext.getUserFromSessionByToken(token);
        //从session取出用户信息
        if (userId == null) {
            return Json.fail(Json.SESSION_TIMEOUT, "登录已失效");
        } else {
            //每次都从数据库中获取用户
            var user = getById(userId.id);
            //返回登录用户的信息给前台 含用户的所有角色和权限
            var permList = checkPerm(user);
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
                    .data("realName", user.realName)
                    .data("avatar", user.avatar)
                    .data("perms", perms);
        }
    }

    /**
     * 退出登录方法 清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json logout() {
        ScxContext.logoutUser();
        return Json.ok("User Logged Out");
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json findByUsername(User queryUser) {
        var user = findByUsername(queryUser.username);
        if (user == null) {
            return Json.ok().data("success", true);
        } else {
            return Json.ok().data("success", false);
        }
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json avatarUpdate(User queryUser) {
        var currentUser = ScxContext.getCurrentUser();
        currentUser.avatar = queryUser.avatar;
        var b = updateById(currentUser) != null;
        ScxLogService.outLog("更改了头像 用户名是 :" + currentUser.username);
        return Json.ok().data("success", b);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json getUserLog() {
        var currentUser = ScxContext.getCurrentUser();
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
        var currentUser = ScxContext.getCurrentUser();
        currentUser.realName = queryUser.realName;
        currentUser.phoneNumber = queryUser.phoneNumber;
        currentUser.password = queryUser.password;
        currentUser.salt = null;
        var b = updateUserPassword(currentUser) != null;
        scxLogService.recordLog("更新了自己的信息", "用户名是 :" + currentUser.username);
        return Json.ok().data("success", b);
    }

    public User updateUserPassword(User user) {

        if (!StringUtils.isEmpty(user.password)) {
            var passwordAndSalt = encryptPassword(user.password);
            user.password = passwordAndSalt[0];
            user.salt = passwordAndSalt[1];
        } else {
            user.password = null;
        }

        return updateById(user);
    }

    /**
     * 删除恢复方法
     *
     * @param id id
     * @return json
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json revokeDelete(Long id) {
        return Json.ok(revokeDeleteUser(id) != null ? "success" : "error");
    }

    /**
     * 逻辑恢复删除用户
     * 恢复时 同时把权限和角色的对应关联数据标记为已恢复
     *
     * @param id id
     * @return b
     */
    public User revokeDeleteUser(Long id) {
        return updateUserAndDept(id, false);
    }

    public User updateUserAndDept(Long id, boolean b) {
        var user = new User();
        var userRole = new Param<>(new UserRole());
        var userDept = new Param<>(new UserDept());

        user.id = id;
        user.isDeleted = b;


        userRole.queryObject.userId = id;
        userRole.queryObject.isDeleted = b;
        userRole.whereSql = "user_id = " + id;

        userDept.queryObject.userId = id;
        userDept.queryObject.isDeleted = b;
        userDept.whereSql = "user_id = " + id;

        userRoleService.update(userRole);
        userDeptService.update(userDept);
        return updateById(user);
    }

    /**
     * 用户批量删除方法
     *
     * @param params model 的名称
     * @return json bool类型 true 代表删除成功 false 代表删除失败
     */
    @ScxMapping(useMethodNameAsUrl = true)
    @SuppressWarnings("unchecked")
    public Json batchDelete(Map<String, Object> params) {
        var deleteIds = (ArrayList<Long>) params.get("deleteIds");
        for (Long delId : deleteIds) {
            deleteUser(delId, true);
        }
        return Json.ok("success").data("deletedCount", deleteIds.size());
    }

    /**
     * 物理删除 用户
     * 删除时 同时删除 权限和角色的对应关联数据
     *
     * @param id id
     * @return b
     */
    public Boolean deleteUser(Long id, boolean b) {
        var userRole = new Param<>(new UserRole());
        var userDept = new Param<>(new UserDept());
        userRole.queryObject.userId = id;
        userDept.queryObject.userId = id;
        userRoleService.delete(userRole);
        userDeptService.delete(userDept);
        return deleteByIds(id) == 1;
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json register(Map<String, Object> params) {
        var username = (String) params.get("username");
        var password = (String) params.get("password");
        var newUser = new Param<>(new User());

        newUser.addOrderBy(SortType.ASC);

        newUser.queryObject.username = username;
        User user = get(newUser);
        if (user != null) {
            return Json.ok("userAlreadyExists");
        } else {
            newUser.queryObject.level = 4;
            newUser.queryObject.level = 0;
            newUser.queryObject.password = password;
            registeredUser(newUser.queryObject);
            return Json.ok("registerSuccess");
        }
    }

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

        var list = list(user);
        var count = count(user);
        if (list.size() == 0) {
            return Json.ok().tables(new String[]{}, 0);
        }
        return Json.ok().tables(list, count);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json listSubUser() {
        var userAndDeptTree = new ArrayList<HashMap<String, Object>>();
        var queryUser = new Param<>(new User());
        queryUser.queryObject.level = 4;
        queryUser.addOrderBy("personOrder", SortType.DESC);
        var deptList = deptService.listAll();
        for (User user : list(queryUser)) {
            var tempUserMap = new HashMap<String, Object>();
            tempUserMap.put("id", "u_" + user.id);
            tempUserMap.put("label", user.realName);
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
        var p = new Param<Dept>(new Dept());
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

    private static class LoginError {
        LocalDateTime lastErrorDate;
        Integer errorTimes;
    }
}
