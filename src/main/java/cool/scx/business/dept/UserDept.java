package cool.scx.business.dept;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 用户部门关联表
 */
@ScxModel(tablePrefix = "core")
public class UserDept extends BaseModel {
    public Long userId;//用户的 id

    public Long deptId;//部门的 id
}
