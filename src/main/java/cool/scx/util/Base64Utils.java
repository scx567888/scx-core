package cool.scx.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * <p>Base64Utils class.</p>
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public class Base64Utils {

    /**
     * <p>base64.</p>
     *
     * @param args a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String base64(String args) {
        var decoder = Base64.getEncoder();
        return decoder.encodeToString(args.getBytes(StandardCharsets.UTF_8));
    }
}
