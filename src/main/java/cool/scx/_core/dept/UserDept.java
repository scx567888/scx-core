package cool.scx._core.dept;

import cool.scx.BaseModel;
import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;

/**
 * 用户部门关联表
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class UserDept extends BaseModel {
    /**
     * 用户的 id
     */
    @Column(notNull = true)
    public Long userId;

    /**
     * 部门的 id
     */
    @Column(notNull = true)
    public Long deptId;
}
