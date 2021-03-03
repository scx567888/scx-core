package cool.scx.business.role;

import cool.scx.dao.BaseModel;
import cool.scx.dao.annotation.Column;
import cool.scx.dao.annotation.ScxModel;

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
