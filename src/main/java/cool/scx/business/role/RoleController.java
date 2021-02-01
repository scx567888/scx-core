package cool.scx.business.role;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.Param;
import cool.scx.enumeration.SortType;
import cool.scx.vo.Json;

@ScxController
public class RoleController {

    RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json listRole() {
        var param = new Param<>(new Role());
        param.addOrderBy("roleOrder", SortType.DESC);
        return Json.ok().items(roleService.list(param));
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json updateRole(Role bean) {
        Role parentBean = null;
        if (bean != null) {
            parentBean = roleService.update(bean);
        }
        return Json.ok().items(parentBean);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json saveRole(Role bean) {
        bean.perm = "";
        bean.type = "2";
        bean.id = roleService.save(bean);
        return Json.ok().items(bean);
    }
}
