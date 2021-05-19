package cool.scx.auth;

import cool.scx.annotation.NeedImpl;

import java.util.List;

/**
 * <p>DeptService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@NeedImpl
public interface DeptService {

    /**
     * <p>getDeptListByUser.</p>
     *
     * @param user a {@link cool.scx.auth.User} object.
     * @return a {@link java.util.List} object.
     */
    List<? extends Dept> getDeptListByUser(User user);

    /**
     * <p>saveDeptListWithUserId.</p>
     *
     * @param userId  a {@link java.lang.Long} object.
     * @param deptIds a {@link java.lang.String} object.
     */
    void saveDeptListWithUserId(Long userId, String deptIds);

    /**
     * <p>deleteByUserId.</p>
     *
     * @param id a {@link java.lang.Long} object.
     */
    void deleteByUserId(Long id);

    /**
     * <p>findDeptByUserId.</p>
     *
     * @param userId a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    List<UserDept> findDeptByUserId(Long userId);

}
