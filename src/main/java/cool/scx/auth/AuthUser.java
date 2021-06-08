package cool.scx.auth;

/**
 * 用户类 包含所有人员信息 通过 level 进行区分人员类型 如 学生 教师 等
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public interface AuthUser {

    /**
     * 唯一 ID 认证 session中用来判断用户的唯一标识
     * 请确保不会重复
     *
     * @return 唯一 ID
     */
    String _UniqueID();

    /**
     * 是否为管理员
     * 管理员没有任何权限限制只有登录限制
     *
     * @return 是否为管理员
     */
    Boolean _IsAdmin();

}
