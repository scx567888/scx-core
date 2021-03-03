package cool.scx.business.dept;

import cool.scx.business.user.User;
import cool.scx.service.BaseService;
import cool.scx.service.Param;
import cool.scx.service.annotation.ScxService;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>DeptService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class DeptService extends BaseService<Dept> {

    private final UserDeptService userDeptService;

    /**
     * <p>Constructor for DeptService.</p>
     *
     * @param userDeptService a {@link cool.scx.business.dept.UserDeptService} object.
     */
    public DeptService(UserDeptService userDeptService) {
        this.userDeptService = userDeptService;
    }

    /**
     * <p>getDeptListByUser.</p>
     *
     * @param user a {@link cool.scx.business.user.User} object.
     * @return a {@link java.util.List} object.
     */
    public List<Dept> getDeptListByUser(User user) {
        var userDeptParam = new Param<>(new UserDept());
        userDeptParam.queryObject.userId = user.id;

        var collect = userDeptService.list(userDeptParam).stream().map(UserDept -> UserDept.deptId.toString()).collect(Collectors.joining(","));
        if (!"".equals(collect)) {
            var deptParam = new Param<>(new Dept());
            deptParam.whereSql = " id in (" + collect + ")";
            return list(deptParam);
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * <p>saveDeptListWithUserId.</p>
     *
     * @param userId  a {@link java.lang.Long} object.
     * @param deptIds a {@link java.lang.String} object.
     */
    public void saveDeptListWithUserId(Long userId, String deptIds) {
        if (!StringUtils.isEmpty(deptIds)) {
            var idArr = Arrays.stream(deptIds.split(",")).filter(id -> !StringUtils.isEmpty(id)).map(id -> {
                        var userDept = new UserDept();
                        userDept.userId = userId;
                        userDept.deptId = Long.parseLong(id);
                        return userDept;
                    }
            ).collect(Collectors.toList());
            userDeptService.saveList(idArr);
        }
    }

    /**
     * <p>deleteByUserId.</p>
     *
     * @param id a {@link java.lang.Long} object.
     */
    public void deleteByUserId(Long id) {
        var userDept = new Param<>(new UserDept());
        userDept.queryObject.userId = id;
        userDeptService.delete(userDept);
    }

    /**
     * <p>findDeptByUserId.</p>
     *
     * @param userId a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    public List<UserDept> findDeptByUserId(Long userId) {
        if (StringUtils.isNotEmpty(userId)) {
            var ud = new Param<>(new UserDept());
            ud.queryObject.userId = userId;
            return userDeptService.list(ud);
        }
        return new ArrayList<>();
    }
}
