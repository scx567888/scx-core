package cool.scx._core.auth.impl;

import cool.scx.annotation.ScxService;
import cool.scx.auth.*;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.exception.AuthException;
import cool.scx.exception.TooManyErrorsException;
import cool.scx.exception.UnknownUserException;
import cool.scx.exception.WrongPasswordException;
import cool.scx.util.CryptoUtils;
import cool.scx.util.LogUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@ScxService
public class CoreUserService extends BaseService<CoreUser> implements UserService {

    private static final HashMap<String, LoginError> loginErrorMap = new HashMap<>();

    private final DeptService deptService;
    private final RoleService roleService;

    private final UserDeptService userDeptService;
    private final UserRoleService userRoleService;

    public CoreUserService(DeptService deptService, RoleService roleService, UserDeptService userDeptService, UserRoleService userRoleService) {
        this.deptService = deptService;
        this.roleService = roleService;
        this.userDeptService = userDeptService;
        this.userRoleService = userRoleService;
    }

    @Override
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
            LogUtils.recordLog(ip + " : 错误登录次数过多");
            var duration = Duration.between(now, loginError.lastErrorDate).toSeconds();
            throw new TooManyErrorsException(duration);
        }
    }

    @Override
    public User findByUsername(String username) {
        var param = new Param<>(new CoreUser());
        param.queryObject.username = username;
        return get(param);
    }

    @Override
    public HashSet<String> getPermStrByUser(User user) {
        var permList = new HashSet<String>();
        //如果是超级管理员或管理员 直接设置为 *
        if (user.level < 5) {
            permList.add("*");
        } else {
            roleService.getRoleListByUser(user).stream().filter(role -> StringUtils.isNotEmpty(role.perms)).map(role -> role.perms).forEach(permList::addAll);
            deptService.getDeptListByUser(user).stream().filter(dept -> StringUtils.isNotEmpty(dept.perms)).map(Dept -> Dept.perms).forEach(permList::addAll);
            //这里无论 是否有权限 都要给一个最基本的首页权限 不然用户进不去首页
            permList.add("/dashboard");
        }
        return permList;
    }

    @Override
    public User updateUserPassword(User user) {
        var coreUser = new CoreUser();
        if (!StringUtils.isEmpty(user.password)) {
            var passwordAndSalt = encryptPassword(user.password);
            coreUser.password = passwordAndSalt[0];
            coreUser.salt = passwordAndSalt[1];
        } else {
            coreUser.password = null;
        }
        return update(coreUser);
    }

    @Override
    public String[] encryptPassword(String password) {
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

    @Override
    public void saveUserDeptIds(Long userId, String deptIds) {
        deptService.saveDeptListWithUserId(userId, deptIds);
    }

    @Override
    public void saveUserRoleIds(Long userId, String roleIds) {
        roleService.saveRoleListWithUserId(userId, roleIds);
    }

    @Override
    public User registeredUser(User user) {
        var deptIds = user.deptIds;
        var roleIds = user.roleIds;
        var passwordAndSalt = encryptPassword(user.password);
        var coreUser = new CoreUser();
        coreUser.password = passwordAndSalt[0];
        coreUser.salt = passwordAndSalt[1];
        var newUser = save(coreUser);
        saveUserDeptIds(newUser.id, deptIds);
        saveUserRoleIds(newUser.id, roleIds);
        return newUser;
    }

    @Override
    public Boolean deleteUser(Long id) {
        deptService.deleteByUserId(id);
        roleService.deleteByUserId(id);
        return deleteByIds(id) == 1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public List<CoreUser> listAll() {
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

    @Override
    public User updateUser(User user) {
        var coreUser = new CoreUser();
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
        deleteUserDept.whereSql = "user_id =" + user.id;
        userDeptService.delete(deleteUserDept);

        var deleteUserRole = new Param<>(new UserRole());
        deleteUserRole.whereSql = "user_id =" + user.id;
        userRoleService.delete(deleteUserRole);
        saveUserDeptIds(user.id, deptIds);
        saveUserRoleIds(user.id, roleIds);
        var update = update(coreUser);
        update.roleIds = user.roleIds;
        update.deptIds = user.deptIds;
        return update;
    }

    @Override
    public User revokeDeleteUser(Long id) {
        return updateUserAndDept(id, false);
    }

    @Override
    public User updateUserAndDept(Long id, boolean b) {
        var user = new CoreUser();
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

    private boolean notHaveLoginError(String ip, LoginError loginError) {
        if (LocalDateTime.now().isBefore(loginError.lastErrorDate)) {
            return false;
        } else if (loginError.errorTimes >= ScxConfig.loginErrorLockTimes()) {
            LoginError le = new LoginError(LocalDateTime.now().plusSeconds(ScxConfig.loginErrorLockSecond()), 0);
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
