package cool.scx.service.dept;

import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.enumeration.SortType;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;

import java.util.Objects;

@ScxService
public class DeptService extends BaseService<Dept> {

    public Json listDept() {
        var p = new Param<>(new Dept());
        p.addOrderBy("level", SortType.DESC)
                .addOrderBy("deptOrder", SortType.DESC);
        var deptList = list(p);
        return Json.ok().items(deptList);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Json saveDept(Dept bean) {
        if (bean != null) {
            Dept parentBean = getById(bean.parentId);
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
            Long save = save(bean);
            bean.id = save;
        }
        return Json.ok().items(bean);
    }

    public Json updateDept(Dept bean) {
        Dept parentBean = null;
        if (bean != null) {
            if (bean.parentId != null && StringUtils.isNotEmpty(bean.parentId) && !(0 == bean.parentId)) {
                parentBean = getById(bean.parentId);
                if (parentBean != null && parentBean.perm != null && !"".equals(parentBean.perm)) {
                    parentBean.perm = "";
                    update(new Param<>(parentBean));
                }
                String str;
                if (Objects.requireNonNull(parentBean).parentStr != null && !"".equals(parentBean.parentStr)) {
                    str = parentBean.parentStr + parentBean.id + ";";
                } else {
                    str = ";" + parentBean.id + ";";
                }
                bean.parentStr = str;
            }
            update(new Param<>(bean));
        }
        return Json.ok().items(bean).data("parentBean", parentBean);
    }


    public Json delete(Long id) {
        if (StringUtils.isNotEmpty(id)) {
            var dept = new Param<>(new Dept());
            dept.queryObject.id = id;
            delete(dept);
            dept.queryObject.id = null;
            dept.whereSql = " parent_str like '%;" + id + ";%'";
            delete(dept);
        }
        return Json.ok();
    }
}
