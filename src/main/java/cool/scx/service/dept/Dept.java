package cool.scx.service.dept;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 部门
 */
@ScxModel(tablePrefix = "core")
public class Dept extends BaseModel {

    public String deptName;//部门名称

    public String perm;//部门权限

    public Integer level;//部门级别

    public Long parentId;//父id

    public String deptOrder;//排序字段

    public String parentStr;//父字符串
}
