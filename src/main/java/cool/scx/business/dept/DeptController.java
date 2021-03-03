package cool.scx.business.dept;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Param;
import cool.scx.enumeration.SortType;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;

import java.util.Objects;

/**
 * <p>DeptController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class DeptController {

    private final DeptService deptService;

    /**
     * <p>Constructor for DeptController.</p>
     *
     * @param deptService a {@link cool.scx.business.dept.DeptService} object.
     */
    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * <p>saveDept.</p>
     *
     * @param bean a {@link cool.scx.business.dept.Dept} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
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
            bean.id = deptService.save(bean).id;
        }
        return Json.ok().items(bean);
    }

    /**
     * <p>listDept.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    public Json listDept() {
        var p = new Param<>(new Dept());
        p.addOrderBy("level", SortType.DESC)
                .addOrderBy("deptOrder", SortType.DESC);
        var deptList = deptService.list(p);
        return Json.ok().items(deptList);
    }

    /**
     * <p>updateDept.</p>
     *
     * @param bean a {@link cool.scx.business.dept.Dept} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    public Json updateDept(Dept bean) {
        Dept parentBean = null;
        if (bean != null) {
            if (StringUtils.isNotEmpty(bean.parentId) && !(0 == bean.parentId)) {
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


    /**
     * <p>delete.</p>
     *
     * @param id a {@link java.lang.Long} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
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
