package cool.scx._core._auth.role;

import cool.scx._core._auth.user.User;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RoleService
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@ScxService
public class RoleService extends BaseService<Role> {

    private final UserRoleService userRoleService;

    /**
     * <p>Constructor for CoreRoleService.</p>
     *
     * @param userRoleService a {@link cool.scx._core._auth.role.UserRoleService} object.
     */
    public RoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * getRoleListByUser
     *
     * @param user a {@link cool.scx._core._auth.user.User} object
     * @return a {@link java.util.List} object
     */
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

    /**
     * getUserRoleByUserIds
     *
     * @param userIds a {@link cool.scx._core._auth.user.User} object
     * @return a {@link java.util.List} object
     */
    public List<UserRole> getUserRoleByUserIds(List<Long> userIds) {
        var p = new Param<>(new UserRole());
        var userIdsStr = userIds.stream().map(Object::toString).collect(Collectors.joining(","));
        if (!"".equals(userIdsStr)) {
            p.whereSql = " user_id in (" + userIdsStr + ")";
            return userRoleService.list(p);
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * saveRoleListWithUserId
     *
     * @param userId  a {@link java.lang.Long} object
     * @param roleIds a {@link java.lang.String} object
     */
    public void saveRoleListWithUserId(Long userId, List<Long> roleIds) {
        if (!StringUtils.isEmpty(roleIds)) {
            var idArr = roleIds.stream().filter(id -> !StringUtils.isEmpty(id)).map(id -> {
                        var userRole = new UserRole();
                        userRole.userId = userId;
                        userRole.roleId = id;
                        return userRole;
                    }
            ).collect(Collectors.toList());
            userRoleService.saveList(idArr);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param id a {@link java.lang.Long} object
     */
    public void deleteByUserId(Long id) {
        var userDept = new Param<>(new UserRole());
        userDept.queryObject.userId = id;
        userRoleService.delete(userDept);
    }

    /**
     * {@inheritDoc}
     *
     * @param userId a {@link java.lang.Long} object
     * @return a {@link java.util.List} object
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
