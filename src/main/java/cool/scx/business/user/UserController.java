package cool.scx.business.user;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.Param;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.business.license.LicenseService;
import cool.scx.business.system.ScxLogService;
import cool.scx.business.user.exception.AuthException;
import cool.scx.business.user.exception.TooManyErrorsException;
import cool.scx.business.user.exception.UnknownUserException;
import cool.scx.business.user.exception.WrongPasswordException;
import cool.scx.enumeration.HttpMethod;
import cool.scx.enumeration.SortType;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ScxController("api/user")
public class UserController {

    private final UserService userService;
    private final LicenseService licenseService;

    public UserController(UserService userService, LicenseService licenseService) {
        this.userService = userService;
        this.licenseService = licenseService;
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
            var loginUser = userService.dologin(username, password);
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

    @ScxMapping(value = "info/:token", httpMethod = HttpMethod.GET)
    public Json info(String token) {
        var userId = ScxContext.getUserFromSessionByToken(token);
        //从session取出用户信息
        if (userId == null) {
            return Json.fail(Json.SESSION_TIMEOUT, "登录已失效");
        } else {
            //每次都从数据库中获取用户
            var user = userService.getById(userId.id);
            //返回登录用户的信息给前台 含用户的所有角色和权限
            var permList = userService.checkPerm(user);
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
            userService.deleteUser(delId, true);
        }
        return Json.ok("success").data("deletedCount", deleteIds.size());
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json register(Map<String, Object> params) {
        var username = (String) params.get("username");
        var password = (String) params.get("password");
        var newUser = new Param<>(new User());

        newUser.addOrderBy(SortType.ASC);

        newUser.queryObject.username = username;
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
}
