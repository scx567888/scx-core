package cool.scx._module.auth;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 用户部门关联表
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "auth")
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
