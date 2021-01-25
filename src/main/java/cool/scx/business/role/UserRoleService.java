package cool.scx.business.role;

import cool.scx.annotation.ScxService;
import cool.scx.business.user.User;
import cool.scx.base.BaseService;
import cool.scx.base.Param;

import java.util.List;

@ScxService
public class UserRoleService extends BaseService<UserRole> {

    public List<UserRole> getListByUser(User user) {
        var userRole = new UserRole();
        userRole.userId = user.id;
        return super.list(new Param<>(userRole));
    }

}
