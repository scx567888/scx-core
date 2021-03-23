package cool.scx.core.role;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 角色
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class Role extends BaseModel {

    public String roleName;//角色名称

    @Column(type = "TEXT")
    public String perm;//权限

    public String type;//角色状态：1,角色，2：临时组

}