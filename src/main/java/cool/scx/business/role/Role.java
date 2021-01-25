package cool.scx.business.role;

import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 角色
 */
@ScxModel(tablePrefix = "core")
public class Role extends BaseModel {

    public String roleName;//角色名称

    @Column(type = "TEXT")
    public String perm;//权限

    public String roleOrder;//角色的排序字段

    @NoColumn
    public Integer parentId = 0;//父id

    public String type;//角色状态：1,角色，2：临时组

}
