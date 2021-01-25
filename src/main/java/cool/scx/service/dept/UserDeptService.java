package cool.scx.service.dept;

import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.enumeration.HttpMethod;
import cool.scx.service.user.User;
import cool.scx.base.BaseService;
import cool.scx.base.Param;

import java.util.List;

@ScxService
public class UserDeptService extends BaseService<UserDept> {

    @ScxMapping(useMethodNameAsUrl = true, httpMethod = HttpMethod.GET)
    public List<UserDept> getListByUser(User user) {
        var param = new Param<>(new UserDept());
        param.queryObject.userId = user.id;
        return list(param);
    }
}
