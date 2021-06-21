package cool.scx._test;

import cool.scx._core._auth.user.User;
import cool.scx._core._auth.user.UserService;
import cool.scx._test.car.Car;
import cool.scx._test.car.CarService;
import cool.scx.annotation.ScxMapping;
import cool.scx.auth.ScxAuth;
import cool.scx.bo.Param;
import cool.scx.enumeration.Method;
import cool.scx.util.*;
import cool.scx.vo.*;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 简单测试
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxMapping("/")
public class TestController {

    private final UserService userService;

    private final CarService carService;

    /**
     * TestController
     *
     * @param userService a
     * @param carService
     */
    public TestController(UserService userService, CarService carService) {
        this.userService = userService;
        this.carService = carService;
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

    /**
     * 测试!!!
     */
    @ScxMapping(method = Method.GET)
    public String getRandomCode() {
        return StringUtils.getRandomCode(9999, true);
    }

    /**
     * 测试!!!
     */
    @ScxMapping(method = Method.GET)
    public BaseVo bigJson() throws Exception {
        var users = userService.listAll();
        return Json.ok().put("items", users);
    }

    /**
     * 测试!!!
     */
    @ScxMapping(method = Method.GET)
    public BaseVo allPerms() throws Exception {
        var perms = ScxAuth.getAllScxMappingPerms();
        return Json.ok().put("items", perms);
    }

    /**
     * 测试!!!
     */
    @ScxMapping(method = Method.GET)
    public BaseVo a() throws Exception {
        return Json.ok().put("items", "a");
    }

    /**
     * 测试!!!
     */
    @ScxMapping(value = "a", method = Method.GET)
    public BaseVo b() throws Exception {
        return Json.ok().put("items", "b");
    }

    /**
     * 测试!!!
     */
    @ScxMapping(method = Method.GET)
    public BaseVo testSelectJson() throws Exception {
        var count = carService.count(new Param<>(new Car()));
        if (count < 20) {
            var list = new ArrayList<Car>();
            for (int i = 0; i < 30; i++) {
                Car car = new Car();
                car.name = "小汽车" + i;
                car.tags = List.of("tag" + i, "tag" + (i + 1));
                list.add(car);
            }
            carService.saveList(list);
        }

        var p = new Param<>(new Car());
        //不用考虑顺序
        var s = new ArrayList<String>();
        s.add("tag21");
        s.add("tag20");
        //可以直接构建字符串
//        p.whereSql = " JSON_CONTAINS (tags,'[\"tag21\",\"tag20\"]' ) ";
        //也可以构建 对象并序列化
        p.whereSql = " JSON_CONTAINS (tags,'" + ObjectUtils.beanToJson(s) + "' ) ";
        var g = carService.list(p);

        return Json.ok().put("items", g);
    }

}
