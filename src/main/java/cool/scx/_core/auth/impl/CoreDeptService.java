package cool.scx._core.auth.impl;

import cool.scx.annotation.ScxService;
import cool.scx.auth.*;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ScxService
public class CoreDeptService extends BaseService<CoreDept> implements DeptService {

    private final UserDeptService userDeptService;


    public CoreDeptService(UserDeptService userDeptService) {
        this.userDeptService = userDeptService;
    }

    @Override
    public List<? extends Dept> getDeptListByUser(User user) {
        var userDeptParam = new Param<>(new UserDept());
        userDeptParam.queryObject.userId = user.id;

        var collect = userDeptService.list(userDeptParam).stream().map(UserDept -> UserDept.deptId.toString()).collect(Collectors.joining(","));
        if (!"".equals(collect)) {
            var deptParam = new Param<>(new CoreDept());
            deptParam.whereSql = " id in (" + collect + ")";
            return list(deptParam);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
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

    @Override
    public void deleteByUserId(Long id) {
        var userDept = new Param<>(new UserDept());
        userDept.queryObject.userId = id;
        userDeptService.delete(userDept);
    }

    @Override
    public List<UserDept> findDeptByUserId(Long userId) {
        if (StringUtils.isNotEmpty(userId)) {
            var ud = new Param<>(new UserDept());
            ud.queryObject.userId = userId;
            return userDeptService.list(ud);
        }
        return new ArrayList<>();
    }
}
