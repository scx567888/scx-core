package cool.scx._core.auth;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.base.BaseUser;
import cool.scx.bo.Param;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>CoreRoleService class.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@ScxService
public class CoreRoleService extends BaseService<CoreRole> implements RoleService {
    private final UserRoleService userRoleService;

    /**
     * <p>Constructor for CoreRoleService.</p>
     *
     * @param userRoleService a {@link cool.scx._core.auth.UserRoleService} object.
     */
    public CoreRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /** getRoleListByUser */
    @Override
    public List<? extends Role> getRoleListByUser(BaseUser user) {
        var userRoleParam = new Param<>(new UserRole());
        userRoleParam.queryObject.userId = user.id;

        var collect = userRoleService.list(userRoleParam).stream().map(userRole -> userRole.roleId.toString()).collect(Collectors.joining(","));
        if (!"".equals(collect)) {
            var roleParam = new Param<>(new CoreRole());
            roleParam.whereSql = " id in (" + collect + ")";
            return list(roleParam);
        } else {
            return new ArrayList<>();
        }
    }

    /** saveRoleListWithUserId */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public void deleteByUserId(Long id) {
        var userDept = new Param<>(new UserRole());
        userDept.queryObject.userId = id;
        userRoleService.delete(userDept);
    }

    /** {@inheritDoc} */
    @Override
    public List<UserRole> findRoleByUserId(Long userId) {
        if (StringUtils.isNotEmpty(userId)) {
            var userRole = new Param<>(new UserRole());
            userRole.queryObject.userId = userId;
            return userRoleService.list(userRole);
        }
        return new ArrayList<>();
    }
}
