package cool.scx._test;

import cool.scx._core._auth.user.User;
import cool.scx._core._auth.user.UserService;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Param;
import cool.scx.enumeration.Method;
import cool.scx.util.FileTypeUtils;
import cool.scx.util.HttpUtils;
import cool.scx.util.MD5Utils;
import cool.scx.util.StringUtils;
import cool.scx.vo.BaseVo;
import cool.scx.vo.Binary;
import cool.scx.vo.Download;
import cool.scx.vo.Html;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 简单测试
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxMapping
public class TestController {

    private final UserService userService;

    /**
     * TestController
     *
     * @param userService a
     */
    public TestController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 测试!!!
     *
     * @return 页面
     */
    @ScxMapping(value = "/", method = Method.GET)
    public Html TestIndex() {
        Integer count = userService.count(new Param<>(new User()));
        if (count < 50) {
            var s1 = new ArrayList<User>();
            for (int i = 0; i < 25; i++) {
                var s = new User();
                var uuid = StringUtils.getUUID();
                //测试表情符能否存储
                s.username = uuid + "👶";
                s.nickName = uuid + "🥝";
                s.password = uuid;
                s.salt = uuid;
                s.isAdmin = false;
                s1.add(s);
            }
            userService.saveList(s1);
            for (int i = 0; i < 25; i++) {
                var s = new User();
                var uuid = StringUtils.getUUID();
                s.username = uuid;
                s.password = uuid;
                s.salt = uuid;
                userService.save(s);
            }
        }
        var users = userService.list(new Param<>(new User()).setPagination(100));
        Html index = Html.ofTemplate("index");
        index.add("userList", users);
        index.add("name", "小明");
        index.add("age", 22);
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

    /**
     * 测试!!!
     */
    @ScxMapping(value = "/download", method = Method.GET)
    public Download TestDownload() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 9999; i++) {
            s.append("download ").append(i);
        }
        return new Download(s.toString().getBytes(StandardCharsets.UTF_8), "a.txt");
    }

    /**
     * 测试!!!
     */
    @ScxMapping(value = "/binary", method = Method.GET)
    public BaseVo TestBinary() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 9999; i++) {
            s.append("download ").append(i);
        }
        return new Binary(s.toString().getBytes(StandardCharsets.UTF_8), FileTypeUtils.getFileTypeForExtension("txt"));
    }

    /**
     * 测试!!!
     */
    @ScxMapping(value = "/md5", method = Method.GET)
    public String TestMd5() {
        return MD5Utils.md5("123");
    }

}
