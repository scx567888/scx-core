package cool.scx.business.dept;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.Param;
import cool.scx.enumeration.SortType;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;

import java.util.Objects;

@ScxController
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json saveDept(Dept bean) {
        if (bean != null) {
            Dept parentBean = deptService.getById(bean.parentId);
            if (parentBean != null) {
                String str;
                if (parentBean.parentStr != null && !"".equals(parentBean.parentStr)) {
                    str = parentBean.parentStr + parentBean.id + ";";
                } else {
                    str = ";" + parentBean.id + ";";
                }
                bean.parentStr = str;
            }
            bean.perm = "";
            Long save = deptService.save(bean).id;
            bean.id = save;
        }
        return Json.ok().items(bean);
    }

    public Json listDept() {
        var p = new Param<>(new Dept());
        p.addOrderBy("level", SortType.DESC)
                .addOrderBy("deptOrder", SortType.DESC);
        var deptList = deptService.list(p);
        return Json.ok().items(deptList);
    }

    public Json updateDept(Dept bean) {
        Dept parentBean = null;
        if (bean != null) {
            if (bean.parentId != null && StringUtils.isNotEmpty(bean.parentId) && !(0 == bean.parentId)) {
                parentBean = deptService.getById(bean.parentId);
                if (parentBean != null && parentBean.perm != null && !"".equals(parentBean.perm)) {
                    parentBean.perm = "";
                    deptService.update(new Param<>(parentBean));
                }
                String str;
                if (Objects.requireNonNull(parentBean).parentStr != null && !"".equals(parentBean.parentStr)) {
                    str = parentBean.parentStr + parentBean.id + ";";
                } else {
                    str = ";" + parentBean.id + ";";
                }
                bean.parentStr = str;
            }
            deptService.update(new Param<>(bean));
        }
        return Json.ok().items(bean).data("parentBean", parentBean);
    }


    public Json delete(Long id) {
        if (StringUtils.isNotEmpty(id)) {
            var dept = new Param<>(new Dept());
            dept.queryObject.id = id;
            deptService.delete(dept);
            dept.queryObject.id = null;
            dept.whereSql = " parent_str like '%;" + id + ";%'";
            deptService.delete(dept);
        }
        return Json.ok();
    }
}
