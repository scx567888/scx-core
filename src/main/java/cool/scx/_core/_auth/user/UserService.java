package cool.scx._core._auth.user;

import cool.scx._core._auth.dept.DeptService;
import cool.scx._core._auth.role.RoleService;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.QueryParam;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>CoreUserService class.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@ScxService
public class UserService extends BaseService<User> {


    /**
     * 部门 service
     */
    private final DeptService deptService;
    /**
     * 角色 service
     */
    private final RoleService roleService;


    /**
     * <p>Constructor for CoreUserService.</p>
     *
     * @param deptService a {@link cool.scx._core._auth.dept.DeptService} object.
     * @param roleService a {@link cool.scx._core._auth.role.RoleService} object.
     */
    public UserService(DeptService deptService, RoleService roleService) {
        this.deptService = deptService;
        this.roleService = roleService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     *
     * @param queryParam a {@link cool.scx.bo.QueryParam} object
     * @return a {@link java.util.List} object
     */
    public List<User> listWithRoleAndDept(QueryParam queryParam) {
        List<User> userList = super.list(queryParam);
        var userIds = userList.stream().map(user -> user.id).collect(Collectors.toList());
        var userDeptListFuture = CompletableFuture.supplyAsync(() -> deptService.getUserDeptByUserIds(userIds));
        var userRoleListFuture = CompletableFuture.supplyAsync(() -> roleService.getUserRoleByUserIds(userIds));
        try {
            var userDeptList = userDeptListFuture.get();
            var userRoleList = userRoleListFuture.get();
            return userList.stream().peek(item -> {
                item.deptIds = userDeptList.stream().filter(userDept -> userDept.userId.equals(item.id)).map(deptItem -> deptItem.deptId).collect(Collectors.toList());
                item.roleIds = userRoleList.stream().filter(userRole -> userRole.userId.equals(item.id)).map(deptItem -> deptItem.roleId).collect(Collectors.toList());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>findByUsername.</p>
     *
     * @param username a {@link java.lang.String} object
     * @return a {@link cool.scx._core._auth.user.User} object
     */
    public User findByUsername(String username) {
        var param = new QueryParam();
        param.where.equal("username", username);
        return get(param);
    }

}
