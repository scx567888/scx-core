package cool.scx.test;

import cool.scx.ScxCoreApp;
import cool.scx.boot.ScxApp;
import cool.scx.boot.ScxContext;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class BaseServiceTest {

    @Test
    public static void test1() throws URISyntaxException, IOException, InterruptedException {
        var bean = ScxContext.getBean(UserService.class);
        var users = bean.listByIds(1L, 2L, 3L);
        for (User user : users) {
            System.out.println(user.nickName);
        }
    }

    @Test
    public static void test2() {
        var bean = ScxContext.getBean(UserService.class);
        var users = bean.listAll();
        System.out.println("共查询" + users.size() + "条数据");
    }


    /**
     * 启动服务器
     */
    @BeforeClass
    public void beforeClass() {
        ScxApp.run(ScxCoreApp.class);
    }
}
