package cool.scx.test;

import cool.scx.ScxCoreApp;
import cool.scx.base.BaseService;
import cool.scx.boot.ScxApp;
import cool.scx.boot.ScxContext;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.util.CryptoUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BaseServiceTest {

    @Test
    public static void test1() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder(new URI("http://127.0.0.1:8080/api/user/1"))
                        .headers("Foo", "foovalue", "Bar", "barvalue")
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        int statusCode = response.statusCode();
        String body = response.body();
        System.out.println(statusCode);
        System.out.println(body);
    }

    @Test
    public static void test2() throws URISyntaxException, IOException, InterruptedException {
        UserService bean = ScxContext.getBean(UserService.class);
        List<User> users = bean.listAll();
        for (User user : users) {
            System.out.println(user.nickName);
        }
    }

    @Test
    public static void test3() throws URISyntaxException, IOException, InterruptedException, ClassNotFoundException {
//        BaseService<User> b= new BaseService<User>();
//        List<User> users = b.listAll();
        System.out.println();
//        System.out.println(1/0);
        String s = CryptoUtils.encryptText("12345678");
        User user = new User();
        user.username = "司昌旭";
        user.password = "12345678";


        var path = "C:\\Users\\scx56\\Documents\\123\\object.dat";
//创建对象流并输出到文件object.dat
        var output = new ObjectOutputStream(new FileOutputStream(path));
//将object对象写到文件中
        output.writeObject(user);

        var input = new ObjectInputStream(new FileInputStream(path));
        var object = (User) input.readObject();

        System.out.println(s);
    }

    /**
     * 启动服务器
     */
    @BeforeClass
    public void beforeClass() {
        ScxApp.run(ScxCoreApp.class);
    }
}
