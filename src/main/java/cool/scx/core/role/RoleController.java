package cool.scx.core.role;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Param;
import cool.scx.enumeration.SortType;
import cool.scx.vo.Json;

/**
 * <p>RoleController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class RoleController {

    RoleService roleService;

    /**
     * <p>Constructor for RoleController.</p>
     *
     * @param roleService a {@link cool.scx.core.role.RoleService} object.
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * <p>listRole.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json listRole() {
        var param = new Param<>(new Role());
        param.addOrderBy("roleOrder", SortType.DESC);
        return Json.ok().items(roleService.list(param));
    }

    /**
     * <p>updateRole.</p>
     *
     * @param bean a {@link cool.scx.core.role.Role} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json updateRole(Role bean) {
        Role parentBean = null;
        if (bean != null) {
            parentBean = roleService.update(bean);
        }
        return Json.ok().items(parentBean);
    }

    /**
     * <p>saveRole.</p>
     *
     * @param bean a {@link cool.scx.core.role.Role} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json saveRole(Role bean) {
        bean.perm = "";
        bean.type = "2";
        bean.id = roleService.save(bean).id;
        return Json.ok().items(bean);
    }
}
