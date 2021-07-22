package cool.scx.enumeration;

/**
 * 日志输出类型
 */
public enum LogOutType {
    FILE,
    CONSOLE,
    BOTH;

    public static LogOutType by(String str) {
        if ("FILE".equalsIgnoreCase(str)) {
            return FILE;
        } else if ("CONSOLE".equalsIgnoreCase(str)) {
            return CONSOLE;
        } else if ("BOTH".equalsIgnoreCase(str)) {
            return BOTH;
        } else {
            return null;
        }
    }
}
