package cool.scx.service.role;

import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.enumeration.SortType;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.vo.Json;

@ScxService
public class RoleService extends BaseService<Role> {

    @ScxMapping(useMethodNameAsUrl = true)
    public Json listRole() {
        var param = new Param<>(new Role());
        param.addOrderBy("roleOrder", SortType.DESC);
        return Json.ok().items(list(param));
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json updateRole(Role bean) {
        Role parentBean = null;
        if (bean != null) {
            parentBean = updateById(bean);
        }
        return Json.ok().items(parentBean);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json saveRole(Role bean) {
        bean.perm = "";
        bean.type = "2";
        bean.id = save(bean);
        return Json.ok().items(bean);
    }
}
