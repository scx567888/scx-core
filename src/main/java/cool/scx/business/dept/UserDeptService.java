package cool.scx.business.dept;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.business.user.User;

import java.util.List;

@ScxService
public class UserDeptService extends BaseService<UserDept> {

    public List<UserDept> getListByUser(User user) {
        var param = new Param<>(new UserDept());
        param.queryObject.userId = user.id;
        return list(param);
    }

}
