package cool.scx.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64工具类
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public final class Base64Utils {

    /**
     * 获取 base64
     *
     * @param str a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String base64(String str) {
        var decoder = Base64.getEncoder();
        return decoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
}
