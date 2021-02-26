package cool.scx.util;


import cool.scx.enumeration.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * String工具类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class StringUtils {
    private static final Map<Integer, Color> printColor = new HashMap<>();
    private static int nextPrintColor = 0;

    static {
        var i = 0;
        for (Color value : Color.values()) {
            printColor.put(i, value);
            i = i + 1;
        }
    }

    /**
     * <p>isNotEmpty.</p>
     *
     * @param str a {@link java.lang.Object} object.
     * @return a boolean.
     */
    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    /**
     * <p>isEmpty.</p>
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
     * <p>getUUID.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * <p>print.</p>
     *
     * @param str       a {@link java.lang.String} object.
     * @param ansiColor a {@link cool.scx.enumeration.Color} object.
     */
    public static void print(String str, Color ansiColor) {
        System.err.print("\u001B[" + ansiColor.toString() + "m" + str + "\u001B[0m");
    }

    /**
     * <p>println.</p>
     *
     * @param str       a {@link java.lang.String} object.
     * @param ansiColor a {@link cool.scx.enumeration.Color} object.
     */
    public static void println(String str, Color ansiColor) {
        System.err.println("\u001B[" + ansiColor.toString() + "m" + str + "\u001B[0m");
    }

    /**
     * 向控制台打印 颜色自动
     *
     * @param str a {@link java.lang.String} object.
     */
    public static void println(String str) {
        if (nextPrintColor >= printColor.size()) {
            nextPrintColor = 0;
        }
        System.err.println("\u001B[" + printColor.get(nextPrintColor).toString() + "m" + str + "\u001B[0m");
        nextPrintColor = nextPrintColor + 1;
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
