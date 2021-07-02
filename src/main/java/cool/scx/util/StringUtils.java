package cool.scx.util;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * String工具类
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class StringUtils {

    /**
     * 校验字符串是否不为空
     *
     * @param str 待检查的字符串
     * @return a boolean.
     */
    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    /**
     * 校验字符串是否为空
     *
     * @param object a {@link java.lang.Object} object.
     * @return a boolean.
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        var str = object.toString();
        if (str.length() == 0) {
            return true;
        }
        for (int i = 0; i < str.length(); i = i + 1) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取UUID
     *
     * @return a {@link java.lang.String} object.
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取随机的 Code
     * 注意!!! 此方法和 getUUID 不同 若需要获取 uuid 请使用 getUUID
     *
     * @param size       code 的长度
     * @param withLetter code 中是否包含字母
     * @return a {@link java.lang.String} object
     */
    public static String getRandomCode(int size, boolean withLetter) {
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] charsWithLetter = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        var rand = new Random();
        var code = new StringBuilder();
        if (withLetter) {
            for (int j = 0; j < size; j++) {
                code.append(charsWithLetter[rand.nextInt(charsWithLetter.length)]);
            }
        } else {
            for (int j = 0; j < size; j++) {
                code.append(chars[rand.nextInt(chars.length)]);
            }
        }
        return code.toString();
    }

    /**
     * 清理分隔符错误的路径如 清理前 : a/b//c -- 清理后 : /a/b/c
     *
     * @param url 需要清理的 url 集合
     * @return 清理后的结果
     */
    public static String clearHttpUrl(String... url) {
        return Arrays.stream(String.join("/", url).split("/")).filter(s -> !"".equals(s)).collect(Collectors.joining("/", "/", ""));
    }

}
