package cool.scx._ext.organization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;
import cool.scx.auth.AuthUser;
import cool.scx.base.BaseModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核心用户类 (演示用,不要用于真实生产环境)
 *
 * @author scx567888
 * @version 1.1.2
 */
@ScxModel(tablePrefix = "auth")
public class User extends BaseModel implements AuthUser {

    /**
     * 登录名，创建后不可改
     */
    @Column(notNull = true, useLike = true, unique = true, excludeOnUpdate = true)
    public String username;

    /**
     * 已加密的登录密码
     */
    @Column(notNull = true)
    @JsonIgnore
    public String password;

    /**
     * 昵称
     */
    @Column(useLike = true)
    public String nickname;

    /**
     * 用户头像 id 此处存储的是 位于 uploadFile 表中的 id
     */
    public String avatar;

    /**
     * 最后一次登录成功的时间
     */
    @JsonIgnore
    public LocalDateTime lastLoginDate;

    /**
     * 最后一次登录成功的IP
     */
    @JsonIgnore
    public String lastLoginIP;

    /**
     * 是否为超级管理员
     */
    @Column(notNull = true, defaultValue = "false")
    @JsonIgnore
    public Boolean isAdmin;

    /**
     * dept id 集合
     */
    @NoColumn
    @JsonIgnore
    public List<Long> deptIds;

    /**
     * role id 集合
     */
    @NoColumn
    @JsonIgnore
    public List<Long> roleIds;

    /**
     * {@inheritDoc}
     */
    @Override
    public String _UniqueID() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean _IsAdmin() {
        return isAdmin;
    }

}
