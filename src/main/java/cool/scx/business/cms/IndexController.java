package cool.scx.business.cms;

import cool.scx.annotation.BodyParam;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.Param;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.enumeration.HttpMethod;
import cool.scx.vo.Html;
import cool.scx.vo.Json;

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
    @ScxMapping(value = "/:name", httpMethod = HttpMethod.GET, unCheckedLogin = true)
    public Json Index(String name, Long age, @BodyParam User user) {
        var users = userService.list(new Param<>(new User()).setPagination(1000));
        Html index = new Html("index");
        index.add("userList", users);
        index.add("name", name);
        index.add("age", age);
        return Json.ok().data("a", name).data("age", age);
    }

}
