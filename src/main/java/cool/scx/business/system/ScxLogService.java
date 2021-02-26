package cool.scx.business.system;

import cool.scx.annotation.http.ScxMapping;
import cool.scx.annotation.service.ScxService;
import cool.scx.base.service.BaseService;
import cool.scx.base.service.Param;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.RequestMethod;
import cool.scx.util.NetUtils;
import io.vertx.ext.web.RoutingContext;

/**
 * <p>ScxLogService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class ScxLogService extends BaseService<ScxLog> {


    /**
     * 强制输出 例如错误日志  不受配置文件影响
     *
     * @param o 输出的语句
     * @param b 是否强制输出
     */
    public static void outLog(Object o, boolean b) {
        System.err.println(o);
    }

    /**
     * 只在控制台打印日志
     *
     * @param o 日志内容
     */
    public static void outLog(Object o) {
        if (ScxConfig.showLog) {
            System.err.println(o);
        }
    }

    /**
     * <p>getName.</p>
     *
     * @param name a {@link java.lang.Long} object.
     * @param age  a {@link java.lang.Integer} object.
     */
    @ScxMapping(value = ":name", method = RequestMethod.GET)
    public void getName(Long name, Integer age) {
        var a = new Param<>(new ScxLog());
        a.setPagination(10);
        get(a);
        System.out.println(name + "--" + age);
    }

    /**
     * 将日志打印并记录到数据库
     *
     * @param o o
     */
    public void outAndRecordLog(Object o) {
        outLog(o);
        recordLog((String) o);
    }

    /**
     * <p>recordLog.</p>
     *
     * @param title a {@link java.lang.String} object.
     */
    public void recordLog(String title) {
//        recordLog(title, title);
    }


    /**
     * 记录日志到数据库
     *
     * @param title   日志标题
     * @param content 日志内容
     * @param ctx     object.
     */
    public void recordLog(String title, String content, RoutingContext ctx) {
        if (ScxConfig.showLog) {
            var log = new ScxLog();
            log.userIp = NetUtils.getIpAddr(ctx);
            try {
                log.username = ScxContext.getLoginUserByHeader(ctx).username;
                log.type = 1;
            } catch (Exception e) {
                log.username = "系统日志";
                log.type = 0;
            }
            log.title = title;
            log.content = content;
            save(log);
        }
    }
}
