package cool.scx.core.cms;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Param;
import cool.scx.context.ScxContext;
import cool.scx.core.user.User;
import cool.scx.core.user.UserService;
import cool.scx.enumeration.Method;
import cool.scx.util.file.FileType;
import cool.scx.util.file.FileUtils;
import cool.scx.vo.Html;
import io.vertx.core.http.Cookie;

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
     * @param userService a {@link cool.scx.core.user.UserService} object.
     */
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 跳转至首页 测试
     *
     * @param user a {@link cool.scx.core.user.User} object.
     * @return 页面
     */
    @ScxMapping(value = "/", method = Method.GET)
    public Html Index(User user) {
        var routingContext = ScxContext.routingContext();

        Map<String, Cookie> stringCookieMap = routingContext.cookieMap();
        FileType fileTypeByHead = FileUtils.getFileTypeByHead(new File("C:\\Users\\scx56\\Desktop\\1"));
        var users = userService.list(new Param<>(new User()).setPagination(1000));
        Html index = new Html("index");
        index.add("userList", users);
        index.add("name", "name");
        index.add("age", "age");
        return index;
    }

}
