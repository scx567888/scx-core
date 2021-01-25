package cool.scx.service.role;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 用户角色关联表
 */
@ScxModel(tablePrefix = "core")
public class UserRole extends BaseModel {

    public Long userId;//用户的 id

    public Long roleId;//角色的 id

}
