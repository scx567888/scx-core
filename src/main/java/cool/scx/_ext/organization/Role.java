package cool.scx._ext.organization;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.List;

/**
 * 角色
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "auth")
public class Role extends BaseModel {

    /**
     * 角色名称
     */
    public String roleName;

    /**
     * 角色权限
     */
    public List<String> perms;

}
