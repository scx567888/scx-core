package cool.scx.util;


import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * String工具类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class StringUtils {

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
     * 驼峰模式字符串转换为下划线字符串 <br>
     * 如：camelStr:"UserInfo"    separator:'_' <br>
     * return "user_info"
     *
     * @param camelStr 驼峰字符串
     * @return str
     */
    public static String camel2Underscore(String camelStr) {
        if (isEmpty(camelStr)) {
            return camelStr;
        }
        var stringBuilder = new StringBuilder();
        char[] strChar = camelStr.toCharArray();
        for (int i = 0, len = strChar.length; i < len; i++) {
            char c = strChar[i];
            if (!Character.isLowerCase(c)) {
                if (i == 0) {
                    stringBuilder.append(Character.toLowerCase(c));
                    continue;
                }
                stringBuilder.append('_').append(Character.toLowerCase(c));
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
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
     * 清理分隔符错误的路径如 清理前 : a/b//c -- 清理后 : /a/b/c
     *
     * @param url 需要清理的 url 集合
     * @return 清理后的结果
     */
    public static String clearHttpUrl(String... url) {
        return Arrays.stream(String.join("/", url).split("/")).filter(s -> !"".equals(s)).collect(Collectors.joining("/", "/", ""));
    }

}
