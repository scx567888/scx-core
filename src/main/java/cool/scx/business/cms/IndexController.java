package cool.scx.business.cms;


import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.Param;
import cool.scx.boot.ScxContext;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.enumeration.HttpMethod;
import cool.scx.vo.Html;

/**
 * <p>IndexController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class IndexController {

    private UserService userService = ScxContext.getBean(UserService.class);

    /**
     * 跳转至首页 测试
     *
     * @param name 测试参数
     * @param age  测试参数
     * @return 页面
     */
    @ScxMapping(value = "/", httpMethod = {HttpMethod.POST, HttpMethod.GET})
    public Html Index(Long name, String age) {
//        try {
//            Thread.sleep(1000);   // 耗时任务
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        var s=new ArrayList<User>();
//        for (int i = 0; i < 99999; i++) {
//            User u=new User();
//            u.username="司昌旭"+i;
//            u.password="password"+i;
//            u.salt="123"+i;
//            u.level=2;
//            s.add(u);
//        }
//
//        userService.saveList(s);
        var users = userService.list(new Param<>(new User()).setPagination(1000));
        Html index = new Html("index");
        index.add("userList", users);
        return index;
    }

}
