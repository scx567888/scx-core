package cool.scx.business.cms;

import cool.scx.annotation.BodyParam;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.Param;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.enumeration.HttpMethod;
import cool.scx.util.FileType;
import cool.scx.util.FileUtils;
import cool.scx.vo.Html;

import java.io.File;
import java.util.Map;

/**
 * <p>IndexController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class IndexController {

    private final UserService userService;

    /**
     * <p>Constructor for IndexController.</p>
     *
     * @param userService a {@link cool.scx.business.user.UserService} object.
     */
    public IndexController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 跳转至首页 测试
     *
     * @param user a {@link cool.scx.business.user.User} object.
     * @return 页面
     */
    @ScxMapping(value = "/", httpMethod = HttpMethod.GET, unCheckedLogin = true)
    public Html Index(@BodyParam Map user) {
        FileType fileTypeByHead = FileUtils.getFileTypeByHead(new File("C:\\Users\\scx56\\Desktop\\1"));
        System.out.println();
        System.out.println();
        var users = userService.list(new Param<>(new User()).setPagination(1000));
        Html index = new Html("index");
        index.add("userList", users);
        index.add("name", "name");
        index.add("age", "age");
        return index;
    }

}
