package cool.scx.util;

import cool.scx.business.system.ScxLog;
import cool.scx.business.system.ScxLogService;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>LogUtils class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class LogUtils {

    private static final Map<Integer, Color> printColor = new HashMap<>();
    /**
     * Constant <code>scxLogService</code>
     */
    public static ScxLogService scxLogService;
    /**
     * Constant <code>showLog=</code>
     */
    public static boolean showLog;
    private static int nextPrintColor = 0;

    static {
        var i = 0;
        for (Color value : Color.values()) {
            printColor.put(i, value);
            i = i + 1;
        }
    }

    /**
     * 只在控制台打印日志
     *
     * @param o 日志内容
     */

    /**
     * 记录日志到数据库
     *
     * @param title   日志标题
     * @param content 日志内容
     * @param ctx     object.
     */
    public static void recordLog(String title, String content) {
        if (showLog) {
            println(title);
        }
        var log = new ScxLog();
        log.userIp = NetUtils.getIpAddr();
        try {
            log.username = ScxContext.getLoginUserByHeader().username;
            log.type = 1;
        } catch (Exception e) {
            log.username = "系统日志";
            log.type = 0;
        }
        log.title = title;
        log.content = content;
        scxLogService.save(log);
    }

    /**
     * <p>recordLog.</p>
     *
     * @param str a {@link java.lang.String} object.
     */
    public static void recordLog(String str) {
        recordLog(str, str);
    }

    /**
     * 输出日志 不换行
     *
     * @param str       a {@link java.lang.String} object.
     * @param ansiColor a {@link cool.scx.enumeration.Color} object.
     */
    public static void print(String str, Color ansiColor) {
        System.err.print("\u001B[" + ansiColor.toString() + "m" + str + "\u001B[0m");
    }

    /**
     * 输出日志
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
}
