package cool.scx.auth;

import cool.scx.annotation.NeedImpl;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;
import cool.scx.context.ScxContext;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>RoleService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
@NeedImpl
public abstract class RoleService extends BaseService<Role> {

    private final UserRoleService userRoleService;

    /**
     * <p>Constructor for RoleService.</p>
     *
     * @param userRoleService a {@link UserRoleService} object.
     */
    public RoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * <p>getRoleListByUser.</p>
     *
     * @param user a {@link cool.scx.base.BaseUser} object.
     * @return a {@link java.util.List} object.
     */
    public List<Role> getRoleListByUser(User user) {
        var userRoleParam = new Param<>(new UserRole());
        userRoleParam.queryObject.userId = user.id;

        var collect = userRoleService.list(userRoleParam).stream().map(userRole -> userRole.roleId.toString()).collect(Collectors.joining(","));
        if (!"".equals(collect)) {
            var roleParam = new Param<>(ScxContext.getBean(Role.class));
            roleParam.whereSql = " id in (" + collect + ")";
            return list(roleParam);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * <p>saveRoleListWithUserId.</p>
     *
     * @param userId  a {@link java.lang.Long} object.
     * @param roleIds a {@link java.lang.String} object.
     */
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

    /**
     * <p>deleteByUserId.</p>
     *
     * @param id a {@link java.lang.Long} object.
     */
    public void deleteByUserId(Long id) {
        var userDept = new Param<>(new UserRole());
        userDept.queryObject.userId = id;
        userRoleService.delete(userDept);
    }

    /**
     * <p>findRoleByUserId.</p>
     *
     * @param userId a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    public List<UserRole> findRoleByUserId(Long userId) {
        if (StringUtils.isNotEmpty(userId)) {
            var userRole = new Param<>(new UserRole());
            userRole.queryObject.userId = userId;
            return userRoleService.list(userRole);
        }
        return new ArrayList<>();
    }
}
