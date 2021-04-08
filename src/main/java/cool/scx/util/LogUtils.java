package cool.scx.util;

import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.core.system.ScxLog;
import cool.scx.core.system.ScxLogService;

/**
 * 日志工具类
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class LogUtils {

    /**
     * scxLogService
     */
    public static final ScxLogService scxLogService = new ScxLogService();

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
        var log = new ScxLog();
        log.userIp = NetUtils.getIpAddr();
        try {
            log.username = ScxContext.getLoginUser().username;
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
     * 记录日志
     *
     * @param str a {@link java.lang.String} object.
     */
    public static void recordLog(String str) {
        recordLog(str, str);
    }

}
