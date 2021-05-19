package cool.scx.auth;

import cool.scx.annotation.Column;
import cool.scx.annotation.NeedImpl;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.List;

/**
 * 角色
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@NeedImpl()
public abstract class Role extends BaseModel {
    public String roleName;//角色名称

    @Column(type = "TEXT")
    public List<String> perms;//权限
}
