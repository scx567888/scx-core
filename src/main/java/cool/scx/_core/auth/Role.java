package cool.scx._core.auth;

import cool.scx.base.BaseModel;

import java.util.List;

/**
 * 角色
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public abstract class Role extends BaseModel {
    public String roleName;//角色名称

    public List<String> perms;//权限
}
