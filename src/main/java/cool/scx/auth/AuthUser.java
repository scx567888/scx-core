package cool.scx.auth;

/**
 * 认证用户接口
 * <p>
 * 自定义的用户类如需使用 "核心认证方式" 则需要实现此接口
 *
 * @author scx567888
 * @version 0.3.6
 */
public interface AuthUser {

    /**
     * 唯一 ID 认证 session中用来判断用户的唯一标识
     * <p>
     * 请确保不会重复
     *
     * @return 唯一 ID
     */
    String _UniqueID();

    /**
     * 是否为管理员
     * <p>
     * 管理员是一种特殊的用户
     * <p>
     * 没有任何权限限制,只有登录限制
     *
     * @return 是否为管理员
     */
    Boolean _IsAdmin();

}
