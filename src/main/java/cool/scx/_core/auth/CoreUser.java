package cool.scx._core.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseUser;

import java.time.LocalDateTime;

/**
 * <p>CoreUser class.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@ScxModel(tableName = "core_user")
public class CoreUser extends BaseUser {
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
     * dept id 集合
     */
    @NoColumn
    @JsonIgnore
    public String deptIds;

    /**
     * role id 集合
     */
    @NoColumn
    @JsonIgnore
    public String roleIds;
}
