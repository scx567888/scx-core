package cool.scx.test;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class ControllerTest {

    @Test
    public static void test1() throws URISyntaxException, IOException, InterruptedException {
//        var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(new URI("http://127.0.0.1:8080")).GET().build(), HttpResponse.BodyHandlers.ofString());
//        String body = response.body();
//        System.out.println(body);
    }


    /**
     * 启动服务器
     */
    @BeforeClass
    public void beforeClass() {
//        ScxApp.run(ScxCoreApp.class);
    }
}
