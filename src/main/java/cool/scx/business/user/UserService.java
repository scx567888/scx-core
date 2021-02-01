package cool.scx.business.user;

import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.business.dept.DeptService;
import cool.scx.business.dept.UserDept;
import cool.scx.business.dept.UserDeptService;
import cool.scx.business.role.RoleService;
import cool.scx.business.role.UserRole;
import cool.scx.business.role.UserRoleService;
import cool.scx.business.system.ScxLogService;
import cool.scx.business.user.exception.AuthException;
import cool.scx.business.user.exception.TooManyErrorsException;
import cool.scx.business.user.exception.UnknownUserException;
import cool.scx.business.user.exception.WrongPasswordException;
import cool.scx.enumeration.HttpMethod;
import cool.scx.util.CryptoUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@ScxService
public class UserService extends BaseService<User> {
    private static final HashMap<String, LoginError> loginErrorMap = new HashMap<>();

    private final DeptService deptService;
    private final RoleService roleService;

    private final UserDeptService userDeptService;
    private final UserRoleService userRoleService;

    public UserService(DeptService deptService, RoleService roleService, UserDeptService userDeptService, UserRoleService userRoleService) {
        this.deptService = deptService;
        this.roleService = roleService;
        this.userDeptService = userDeptService;
        this.userRoleService = userRoleService;
    }

    public User login(String username, String password) throws AuthException {
        var now = LocalDateTime.now();
        var ip = NetUtils.getIpAddr();
        var loginError = loginErrorMap.get(ip);
        if (loginError == null) {
            var le = new LoginError(LocalDateTime.now(), 0);
            loginErrorMap.put(ip, le);
            loginError = le;
        }
        if (notHaveLoginError(ip, loginError)) {
            var user = findByUsername(username);
            if (user == null) {
                var le = new LoginError(now, loginError.errorTimes + 1);
                loginErrorMap.put(ip, le);
                throw new UnknownUserException();
            }
            if (!verifyPassword(user, password)) {
                var le = new LoginError(now, loginError.errorTimes + 1);
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
     * 解密密码 返回加密后的密码 和盐值
     * 用于登录或找回密码
     */


    public User findByUsername(String username) {
        var param = new Param<>(new User());
        param.queryObject.username = username;
        return get(param);
    }

    private boolean notHaveLoginError(String ip, LoginError loginError) {
        if (LocalDateTime.now().isBefore(loginError.lastErrorDate)) {
            return false;
        } else if (loginError.errorTimes >= ScxConfig.loginErrorLockTimes) {
            LoginError le = new LoginError(LocalDateTime.now().plusSeconds(ScxConfig.loginErrorLockSecond), 0);
            loginErrorMap.put(ip, le);
            return false;
        }
        return true;
    }


    /**
     * 根据用户获取 权限串
     *
     * @param user 用户
     * @return s
     */
    public List<String> getPermStrByUser(User user) {
        var permList = new ArrayList<String>();
        //如果是超级管理员或管理员 直接设置为 *
        if (user.level < 5) {
            permList.add("*");
        } else {
            var roleListStr = roleService.getRoleListByUser(user).stream().filter(role -> StringUtils.isNotEmpty(role.perm)).map(role -> role.perm).collect(Collectors.joining(";")).split(";");
            permList.addAll(Arrays.asList(roleListStr));
            var deptListStr = deptService.getDeptListByUser(user).stream().filter(dept -> StringUtils.isNotEmpty(dept.perm)).map(Dept -> Dept.perm).collect(Collectors.joining(";")).split(";");
            permList.addAll(Arrays.asList(deptListStr));
        }
        return permList;
    }

    public User updateUserPassword(User user) {
        if (!StringUtils.isEmpty(user.password)) {
            var passwordAndSalt = encryptPassword(user.password);
            user.password = passwordAndSalt[0];
            user.salt = passwordAndSalt[1];
        } else {
            user.password = null;
        }
        return update(user);
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

    /**
     * 根据 userid 保存 dept列表到 数据库
     *
     * @param userId
     * @param deptIds
     */
    private void saveUserDeptIds(Long userId, String deptIds) {
        deptService.saveDeptListWithUserId(userId, deptIds);
    }

    /**
     * 根据 userid 保存 role 列表到 数据库
     *
     * @param userId
     * @param roleIds
     */
    private void saveUserRoleIds(Long userId, String roleIds) {
        roleService.saveRoleListWithUserId(userId, roleIds);
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
     * 物理删除 用户
     * 删除时 同时删除 权限和角色的对应关联数据
     *
     * @param id id
     * @return b
     */
    public Boolean deleteUser(Long id) {
        deptService.deleteByUserId(id);
        roleService.deleteByUserId(id);
        return deleteByIds(id) == 1;
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
        return getPermStrByUser(ScxContext.getCurrentUser());
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
        User update = update(user);
        update.roleIds = user.roleIds;
        update.deptIds = user.deptIds;
        return update;
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
        return update(user);
    }

    /**
     * 用户登录时的校验类 包含最后一次登录错误时间 和 错误次数
     */
    private static class LoginError {
        LocalDateTime lastErrorDate;
        Integer errorTimes;

        public LoginError(LocalDateTime lastErrorDate, Integer errorTimes) {
            this.lastErrorDate = lastErrorDate;
            this.errorTimes = errorTimes;
        }
    }
}
