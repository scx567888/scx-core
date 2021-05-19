package cool.scx.auth;

import cool.scx.annotation.NeedImpl;
import cool.scx.annotation.ScxService;

import java.util.List;

/**
 * <p>RoleService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
@NeedImpl
public interface RoleService {


    /**
     * <p>getRoleListByUser.</p>
     *
     * @param user a {@link cool.scx.base.BaseUser} object.
     * @return a {@link java.util.List} object.
     */
    List<? extends Role> getRoleListByUser(User user);

    /**
     * <p>saveRoleListWithUserId.</p>
     *
     * @param userId  a {@link java.lang.Long} object.
     * @param roleIds a {@link java.lang.String} object.
     */
    void saveRoleListWithUserId(Long userId, String roleIds);

    /**
     * <p>deleteByUserId.</p>
     *
     * @param id a {@link java.lang.Long} object.
     */
    void deleteByUserId(Long id);

    /**
     * <p>findRoleByUserId.</p>
     *
     * @param userId a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    List<UserRole> findRoleByUserId(Long userId);

}
