package cool.scx.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;

/**
 * 用户类 包含所有人员信息 通过 level 进行区分人员类型 如 学生 教师 等
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public abstract class BaseUser extends BaseModel {

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

}
