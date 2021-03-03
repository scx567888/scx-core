package cool.scx.business.role;

import cool.scx.dao.BaseModel;
import cool.scx.dao.annotation.Column;
import cool.scx.dao.annotation.ScxModel;

/**
 * 用户角色关联表
 *
 * @author 司昌旭
 * @version 0.3.6
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
