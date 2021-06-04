package cool.scx._test;

import cool.scx._core.user.User;
import cool.scx._core.user.UserService;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Param;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Method;
import cool.scx.util.HttpUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Html;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>IndexController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class TestController {

    private final UserService coreUserService;

    /**
     * <p>Constructor for IndexController.</p>
     *
     * @param coreUserService a {@link cool.scx._core.user.UserService} object.
     */
    public TestController(UserService coreUserService) {
        this.coreUserService = coreUserService;
    }

    /**
     * 测试!!!
     *
     * @return 页面
     */
    @ScxMapping(value = "/", method = Method.GET)
    public Html TestIndex() {
        Integer count = coreUserService.count(new Param<>(new User()));
        if (count < 50) {
            var s1 = new ArrayList<User>();
            for (int i = 0; i < 50; i++) {
                var s = new User();
                //测试表情符能否存储
                s.username = StringUtils.getUUID() + "👶";
                s.password = StringUtils.getUUID();
                s.salt = StringUtils.getUUID();
                s.isAdmin = false;
                s1.add(s);
            }
            coreUserService.saveList(s1);
            for (int i = 0; i < 50; i++) {
                var s = new User();
                s.username = StringUtils.getUUID();
                s.password = StringUtils.getUUID();
                s.salt = StringUtils.getUUID();
                coreUserService.save(s);
            }
        }
        var users = coreUserService.list(new Param<>(ScxContext.getBean(User.class)).setPagination(1000));
        Html index = Html.ofTemplate("index");
        index.add("userList", users);
        index.add("name", "name");
        index.add("age", 88888);
        return index;
    }


    /**
     * 测试!!!
     */
    @ScxMapping(value = "/baidu", method = Method.GET)
    public Html TestHttpUtils() {
        HttpResponse<String> stringHttpResponse = HttpUtils.get("https://www.baidu.com/", new HashMap<>());
        return Html.ofString(stringHttpResponse.body());
    }

}
