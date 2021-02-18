package cool.scx.business.cms;

import cool.scx.annotation.ScxController;
import cool.scx.base.Param;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.vo.Html;

/**
 * <p>IndexController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 跳转至首页 测试
     *
     * @param name 测试参数
     * @param age  测试参数
     * @return 页面
     */
//    @ScxMapping(value = "/", httpMethod = HttpMethod.GET,unCheckedLogin = true)
    public Html Index(String name, Long age) {
        var users = userService.list(new Param<>(new User()).setPagination(1000));
        Html index = new Html("index");
        index.add("userList", users);
        index.add("name", name);
        index.add("age", age);
        return index;
    }

}
