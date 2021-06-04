package cool.scx._core.role;

import cool.scx.BaseModel;
import cool.scx.annotation.ScxModel;

import java.util.List;

/**
 * 角色
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
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
