package cool.scx.business.role;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.business.user.UserService;

@ScxService
public class UserRoleService extends BaseService<UserRole> {
    UserService userService;

    public UserRoleService(UserService userService) {
        this.userService = userService;
    }
}
