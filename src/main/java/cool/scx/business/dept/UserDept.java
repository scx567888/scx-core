package cool.scx.business.dept;

import cool.scx.dao.annotation.Column;
import cool.scx.dao.annotation.ScxModel;
import cool.scx.dao.BaseModel;

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
