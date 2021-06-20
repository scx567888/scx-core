package cool.scx.util;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CaseUtils {

    /**
     * 切割驼峰命名法的正则表达式
     */
    private static final Pattern CAMEL_PATTERN = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

    /**
     * 转换为驼峰命名法 getNameByAge
     *
     * @param str s
     * @return r
     */
    public static String toCamel(String str) {
        String[] sourceStrings = getSourceStrings(str);
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < sourceStrings.length; i++) {
            if (i == 0) {
                stringBuilder.append(sourceStrings[i].toLowerCase());
            } else {
                stringBuilder.append(toUpperCase(sourceStrings[i].toLowerCase()));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 转换为匈牙利命名法 GetNameByAge
     *
     * @param str s
     * @return r
     */
    public static String toPascal(String str) {
        String[] sourceStrings = getSourceStrings(str);
        var stringBuilder = new StringBuilder();
        for (String sourceString : sourceStrings) {
            stringBuilder.append(toUpperCase(sourceString.toLowerCase()));
        }
        return stringBuilder.toString();
    }

    /**
     * 转换为短横线命名法 get-name-by-age
     *
     * @param str s
     * @return r
     */
    public static String toKebab(String str) {
        String[] sourceStrings = getSourceStrings(str);
        return Arrays.stream(sourceStrings).map(String::toLowerCase).collect(Collectors.joining("-"));
    }

    /**
     * 转换为蛇形命名法 get_name_by_age
     *
     * @param str s
     * @return r
     */
    public static String toSnake(String str) {
        String[] sourceStrings = getSourceStrings(str);
        return Arrays.stream(sourceStrings).map(String::toLowerCase).collect(Collectors.joining("_"));
    }

    /**
     * 判断原来的命名是啥 并返回
     *
     * @param str 源字符串
     * @return 分割后的数组
     */
    public static String[] getSourceStrings(String str) {
        if (StringUtils.isEmpty(str)) {
            return new String[0];
        }
        if (str.contains("_")) {
            return str.split("_");
        } else if (str.contains("-")) {
            return str.split("-");
        } else {
            return CAMEL_PATTERN.split(str);
        }
    }

    private static String toUpperCase(String string) {
        char[] methodName = string.toCharArray();
        if (97 <= methodName[0] && methodName[0] <= 122) {
            methodName[0] ^= 32;
        }
        return String.valueOf(methodName);
    }

    /**
     * 驼峰模式字符串转换为下划线字符串 <br>
     * <p>
     * 如：UserInfo 结果为 user_info  <br>
     * 此方法有 bug 请考虑使用  toSnake
     *
     * @param camelStr 驼峰字符串
     * @return str
     */
    @Deprecated
    public static String camelToSnake(String camelStr) {
        if (StringUtils.isEmpty(camelStr)) {
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

}
