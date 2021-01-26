package cool.scx.business.role;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.business.user.User;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ScxService
public class RoleService extends BaseService<Role> {

    private final UserRoleService userRoleService;

    public RoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    public List<Role> getRoleListByUser(User user) {
        var userRoleParam = new Param<>(new UserRole());
        userRoleParam.queryObject.userId = user.id;

        var collect = userRoleService.list(userRoleParam).stream().map(userRole -> userRole.roleId.toString()).collect(Collectors.joining(","));
        if (!"".equals(collect)) {
            var roleParam = new Param<>(new Role());
            roleParam.whereSql = " id in (" + collect + ")";
            return list(roleParam);
        } else {
            return new ArrayList<>();
        }
    }

    public void saveRoleListWithUserId(Long userId, String roleIds) {
        if (!StringUtils.isEmpty(roleIds)) {
            var idArr = Arrays.stream(roleIds.split(",")).filter(id -> !StringUtils.isEmpty(id)).map(id -> {
                        var userRole = new UserRole();
                        userRole.userId = userId;
                        userRole.roleId = Long.parseLong(id);
                        return userRole;
                    }
            ).collect(Collectors.toList());
            userRoleService.saveList(idArr);
        }
    }

    public void deleteByUserId(Long id) {
        var userDept = new Param<>(new UserRole());
        userDept.queryObject.userId = id;
        userRoleService.delete(userDept);
    }

    public List<UserRole> findRoleByUserId(Long userId) {
        if (StringUtils.isNotEmpty(userId)) {
            var userRole = new Param<>(new UserRole());
            userRole.queryObject.userId = userId;
            return userRoleService.list(userRole);
        }
        return new ArrayList<>();
    }
}
