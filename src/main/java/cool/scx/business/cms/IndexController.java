package cool.scx.business.cms;

import cool.scx.annotation.http.BodyParam;
import cool.scx.annotation.http.ScxMapping;
import cool.scx.base.service.Param;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.RequestMethod;
import cool.scx.util.FileType;
import cool.scx.util.FileUtils;
import cool.scx.vo.Html;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.util.Map;

/**
 * <p>IndexController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
//@ScxController
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

    //    @ScxMapping(value = "/api/:a/b",method = RequestMethod.POST)

    /**
     * <p>aaa.</p>
     *
     * @param userList a {@link cool.scx.business.user.User} object.
     * @return a {@link java.lang.String} object.
     */
    public String aaa(@BodyParam("userList.userList1.userList2") User userList) {
        return "456";
    }

    /**
     * 跳转至首页 测试
     *
     * @return 页面
     */
    @ScxMapping(value = "/", method = RequestMethod.POST)
    public Html Index(User user) {
        var routingContext = ScxContext.routingContext();
        System.out.println();

        Map<String, Cookie> stringCookieMap = routingContext.cookieMap();
        FileType fileTypeByHead = FileUtils.getFileTypeByHead(new File("C:\\Users\\scx56\\Desktop\\1"));
        var users = userService.list(new Param<>(new User()).setPagination(1000));
        Html index = new Html("index");
        index.add("userList", users);
        index.add("name", "name");
        index.add("age", "age");
        return index;
    }

    /**
     * <p>Index1.</p>
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a {@link cool.scx.vo.Html} object.
     */
    @ScxMapping(value = "/1", method = RequestMethod.GET)
    public Html Index1(RoutingContext routingContext) {
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
