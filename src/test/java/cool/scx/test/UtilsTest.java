package cool.scx.test;

import cool.scx.util.CryptoUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class UtilsTest {

    @Test
    public static void test1() throws URISyntaxException, IOException, InterruptedException {
        System.out.println(1 + 1);
    }

    @Test
    public static void test2() {
        System.out.println(2 + 2);
    }

    @Test
    public static void test3() {
        String s = CryptoUtils.encryptText("12345678");
        System.out.println(s);
    }

}
