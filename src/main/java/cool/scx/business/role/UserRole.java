package cool.scx.business.role;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 用户角色关联表
 */
@ScxModel(tablePrefix = "core")
public class UserRole extends BaseModel {

    /**
     * 用户的 id
     */
    @Column(notNull = true)
    public Long userId;

    /**
     * 角色的 id
     */
    @Column(notNull = true)
    public Long roleId;

}
