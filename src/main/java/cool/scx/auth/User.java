package cool.scx.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.OneAndOnlyOneImpl;
import cool.scx.base.BaseModel;

import java.time.LocalDateTime;

/**
 * 用户类 包含所有人员信息 通过 level 进行区分人员类型 如 学生 教师 等
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@OneAndOnlyOneImpl
public abstract class User extends BaseModel {

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
     * 昵称
     */
    @Column(useLike = true)
    public String nickName;
    /**
     * 性别
     */
    public String gender;
    /**
     * 用户头像 id 此处存储的是 位于 uploadFile 表中的 id
     */
    public String avatar;
    /**
     * 电话号码
     */
    public String phone;
    /**
     * 用户级别 共六个级别
     * 2  超级管理员 一个系统应有且只有一个
     * 4  普通管理员
     * 8  教师,商家等
     * 16 普通会员用户
     * 32 普通用户
     * 64 游客
     */
    @Column(notNull = true, defaultValue = "8")
    @JsonIgnore
    public Byte level;
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
