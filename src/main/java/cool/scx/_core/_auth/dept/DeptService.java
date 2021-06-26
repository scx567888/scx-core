package cool.scx._core._auth.dept;

import cool.scx._core._auth.user.User;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;
import cool.scx.bo.Where;
import cool.scx.enumeration.WhereType;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>CoreDeptService class.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@ScxService
public class DeptService extends BaseService<Dept> {

    private final UserDeptService userDeptService;

    /**
     * <p>Constructor for CoreDeptService.</p>
     *
     * @param userDeptService a {@link cool.scx._core._auth.dept.UserDeptService} object.
     */
    public DeptService(UserDeptService userDeptService) {
        this.userDeptService = userDeptService;
    }

    /**
     * getDeptListByUser
     *
     * @param user a {@link cool.scx._core._auth.user.User} object
     * @return a {@link java.util.List} object
     */
    public List<Dept> getDeptListByUser(User user) {
        var userDeptParam = new Query();
//        userDeptParam.o.userId = user.id;

        var collect = userDeptService.list(userDeptParam).stream().map(UserDept -> UserDept.deptId.toString()).collect(Collectors.joining(","));
        if (!"".equals(collect)) {
            var deptParam = new Query();
//            deptParam.whereSql = " id in (" + collect + ")";
            return list(deptParam);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * saveDeptListWithUserId
     *
     * @param userId  a {@link java.lang.Long} object
     * @param deptIds a {@link java.lang.String} object
     */
    public void saveDeptListWithUserId(Long userId, List<Long> deptIds) {
        if (!StringUtils.isEmpty(deptIds)) {
            var idArr = deptIds.stream().filter(id -> !StringUtils.isEmpty(id)).map(id -> {
                        var userDept = new UserDept();
                        userDept.userId = userId;
                        userDept.deptId = id;
                        return userDept;
                    }
            ).collect(Collectors.toList());
            userDeptService.save(idArr);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param id a {@link java.lang.Long} object
     */
    public void deleteByUserId(Long id) {
        var where = new Where("userId", WhereType.EQUAL, id);
        userDeptService.delete(where);
    }

    /**
     * {@inheritDoc}
     *
     * @param userId a {@link java.lang.Long} object
     * @return a {@link java.util.List} object
     */
    public List<UserDept> findDeptByUserId(Long userId) {
        if (StringUtils.isNotEmpty(userId)) {
            var queryParam = new Query().addWhere("userId", WhereType.EQUAL, userId);
            return userDeptService.list(queryParam);
        }
        return new ArrayList<>();
    }

    /**
     * <p>getUserDeptByUserIds.</p>
     *
     * @param userIds a {@link java.util.List} object
     * @return a {@link java.util.List} object
     */
    public List<UserDept> getUserDeptByUserIds(List<Long> userIds) {
        if (userIds != null && userIds.size() > 0) {
            var queryParam = new Query().addWhere("userId", WhereType.IN, userIds);
            return userDeptService.list(queryParam);
        } else {
            return new ArrayList<>();
        }
    }
}
