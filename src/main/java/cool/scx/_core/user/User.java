package cool.scx._core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.BaseModel;
import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;
import cool.scx.auth.AuthUser;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>CoreUser class.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@ScxModel(tablePrefix = "core")
public class User extends BaseModel implements AuthUser {

    /**
     * 性别
     */
    public String gender;

    /**
     * 昵称
     */
    @Column(useLike = true)
    public String nickName;

    /**
     * 用户头像 id 此处存储的是 位于 uploadFile 表中的 id
     */
    public String avatar;

    /**
     * 电话号码
     */
    public String phone;

    /**
     * 最后一次登录时间
     */
    @JsonIgnore
    public LocalDateTime lastLoginDate;

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
     * 随机加密盐值
     */
    @Column(notNull = true)
    @JsonIgnore
    public String salt;

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
    public String _username() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean _isAdmin() {
        return isAdmin;
    }
}
