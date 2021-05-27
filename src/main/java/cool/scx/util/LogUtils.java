package cool.scx.util;

import cool.scx.config.ScxConfig;

/**
 * 日志工具类
 * todo 需要和其他日志系统进行结合
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class LogUtils {

    /**
     * 记录日志到数据库
     *
     * @param title   日志标题
     * @param content 日志内容
     */
    public static void recordLog(String title, String content) {
        if (ScxConfig.showLog()) {
            Ansi.OUT.print(title).ln();
        }
    }

    /**
     * 记录日志
     *
     * @param str a {@link java.lang.String} object.
     */
    public static void recordLog(String str) {
        recordLog(str, str);
    }

}
