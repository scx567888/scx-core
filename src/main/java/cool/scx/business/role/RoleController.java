package cool.scx.business.role;

import cool.scx.dao.type.SortType;
import cool.scx.service.Param;
import cool.scx.web.annotation.ScxController;
import cool.scx.web.annotation.ScxMapping;
import cool.scx.web.vo.Json;

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
     * @param roleService a {@link cool.scx.business.role.RoleService} object.
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * <p>listRole.</p>
     *
     * @return a {@link cool.scx.web.vo.Json} object.
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
     * @param bean a {@link cool.scx.business.role.Role} object.
     * @return a {@link cool.scx.web.vo.Json} object.
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
     * @param bean a {@link cool.scx.business.role.Role} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Json saveRole(Role bean) {
        bean.perm = "";
        bean.type = "2";
        bean.id = roleService.save(bean).id;
        return Json.ok().items(bean);
    }
}
