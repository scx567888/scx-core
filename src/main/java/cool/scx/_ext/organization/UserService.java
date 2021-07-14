package cool.scx._ext.organization;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 核心用户 service
 *
 * @author scx567888
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
     * c
     *
     * @param deptService c
     * @param roleService c
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
     * @param query a {@link cool.scx.bo.Query} object
     * @return a {@link java.util.List} object
     */
    public List<User> listWithRoleAndDept(Query query) {
        List<User> userList = super.list(query);
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

}
