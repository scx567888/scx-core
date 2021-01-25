package cool.scx.business.user;

import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 用户类 包含所有人员信息 通过 type 进行区分人员类型 如 学生 教师 等
 */
@ScxModel(tablePrefix = "core")
public class User extends BaseModel {

    @Column(useLike = true)
    public String realName; //真实姓名

    public String sex; //性别

    @Column(notNull = true, useLike = true, unique = true)
    public String username;// 登录名，不可改

    public String salt;// 加密盐值

    public String password;// 已加密的登录密码

    public String avatar;//用户头像

    @NoColumn
    public String deptIds;//dept id 集合

    @NoColumn
    public String roleIds;//role id 集合

    @NoColumn
    public Integer parentId = 0;//父id

    public String phoneNumber;//电话号码

    //用户级别
    //取值 2 4 8 16 32 64 128 256
    public Integer level;

}
