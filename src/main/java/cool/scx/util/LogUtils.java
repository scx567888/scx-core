package cool.scx.util;

import cool.scx._core.log.Log;
import cool.scx._core.log.LogService;
import cool.scx.auth.ScxAuth;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;

/**
 * 日志工具类
 * todo 需要和其他日志系统进行结合
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class LogUtils {

    private static final LogService logService = ScxContext.getBean(LogService.class);

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
        var log = new Log();
        log.userIp = NetUtils.getIpAddr();
        try {
            log.username = ScxAuth.getLoginUser()._username();
            log.type = 1;
        } catch (Exception e) {
            log.username = "系统日志";
            log.type = 0;
        }
        log.title = title;
        log.content = content;
        logService.save(log);
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
