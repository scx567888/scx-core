package cool.scx.util;


import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * String工具类
 */
public class StringUtils {

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

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
     * 驼峰模式字符串转换为下划线字符串 <br/>
     * 如：camelStr:"UserInfo"    separator:'_' <br/>
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

    public static String toLowerCaseFirstOne(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static void println(String str, Color ansiColor) {
        System.out.println("\u001B[" + ansiColor.toString() + "m" + str + "\u001B[0m");
    }

    public static void print(String str, Color ansiColor) {
        System.out.print("\u001B[" + ansiColor.toString() + "m" + str + "\u001B[0m");
    }

    public static String cleanHttpUrl(String... url) {
        var tempFullUrl = String.join("/", url);
        return Arrays.stream(tempFullUrl.split("/")).filter(s -> !"".equals(s)).collect(Collectors.joining("/", "/", ""));
    }

    public enum Color {

        DEFAULT("39"),

        BLACK("30"),

        RED("31"),

        GREEN("32"),

        YELLOW("33"),

        BLUE("34"),

        MAGENTA("35"),

        CYAN("36"),

        WHITE("37"),

        BRIGHT_BLACK("90"),

        BRIGHT_RED("91"),

        BRIGHT_GREEN("92"),

        BRIGHT_YELLOW("93"),

        BRIGHT_BLUE("94"),

        BRIGHT_MAGENTA("95"),

        BRIGHT_CYAN("96"),

        BRIGHT_WHITE("97");

        private final String code;

        Color(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return this.code;
        }

    }

}
