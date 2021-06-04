package cool.scx.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils {

    public static String base64(String args) {
        var decoder = Base64.getEncoder();
        return decoder.encodeToString(args.getBytes(StandardCharsets.UTF_8));
    }
}
