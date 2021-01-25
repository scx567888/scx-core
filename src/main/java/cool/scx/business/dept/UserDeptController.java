package cool.scx.business.dept;

import cool.scx.annotation.ScxController;

@ScxController
public class UserDeptController {

    private final UserDeptService userDeptService;

    public UserDeptController(UserDeptService userDeptService) {
        this.userDeptService = userDeptService;
    }


}
