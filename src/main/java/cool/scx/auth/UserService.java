package cool.scx.auth;

import cool.scx.annotation.NeedImpl;
import cool.scx.annotation.ScxService;
import cool.scx.exception.AuthException;

import java.util.HashSet;

/**
 * <p>BaseUserService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@NeedImpl
@ScxService
public interface UserService {

    /**
     * <p>login.</p>
     *
     * @param username a {@link String} object.
     * @param password a {@link String} object.
     * @return a {@link cool.scx.auth.User} object.
     * @throws AuthException if any.
     */
    User login(String username, String password) throws AuthException;


    /**
     * 解密密码 返回加密后的密码 和盐值
     * 用于登录或找回密码
     *
     * @param username 用户名
     * @return 寻找到的用户
     */
    User findByUsername(String username);

    /**
     * 根据用户获取 权限串
     * todo 需要做缓存 减少数据库查询压力
     *
     * @param user 用户
     * @return s
     */
    HashSet<String> getPermStrByUser(User user);

    /**
     * <p>updateUserPassword.</p>
     *
     * @param user a {@link cool.scx.auth.User} object.
     * @return a {@link cool.scx.auth.User} object.
     */
    User updateUserPassword(User user);

    /**
     * 加密密码 返回加密后的密码 和盐值
     * 用于新建用户
     */
    String[] encryptPassword(String password);

    /**
     * 根据 userid 保存 dept列表到 数据库
     *
     * @param userId  r
     * @param deptIds r
     */
    void saveUserDeptIds(Long userId, String deptIds);

    /**
     * 根据 userid 保存 role 列表到 数据库
     *
     * @param userId  r
     * @param roleIds r
     */
    void saveUserRoleIds(Long userId, String roleIds);


    /**
     * 注册用户
     *
     * @param user 新建用户
     * @return 新增的 id
     */
    User registeredUser(User user);

    /**
     * 物理删除 用户
     * 删除时 同时删除 权限和角色的对应关联数据
     *
     * @param id id
     * @return b
     */
    Boolean deleteUser(Long id);

    /**
     * <p>updateUser.</p>
     *
     * @param user a {@link cool.scx.auth.User} object.
     * @return a {@link cool.scx.auth.User} object.
     */
    User updateUser(User user);

    /**
     * 逻辑恢复删除用户
     * 恢复时 同时把权限和角色的对应关联数据标记为已恢复
     *
     * @param id id
     * @return b
     */
    User revokeDeleteUser(Long id);

    /**
     * <p>updateUserAndDept.</p>
     *
     * @param id a {@link Long} object.
     * @param b  a boolean.
     * @return a {@link cool.scx.auth.User} object.
     */
    User updateUserAndDept(Long id, boolean b);
}
