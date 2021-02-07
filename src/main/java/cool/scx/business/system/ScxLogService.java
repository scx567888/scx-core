package cool.scx.business.system;

import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.enumeration.HttpMethod;
import cool.scx.util.NetUtils;

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

    @ScxMapping(value = ":name", httpMethod = HttpMethod.GET)
    public void getName(Long name, Integer age) {
        var a = new Param<>(new ScxLog());
        a.setPagination(10);
        listMap(a);
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

    public void recordLog(String title) {
        recordLog(title, title);
    }


    /**
     * 记录日志到数据库
     *
     * @param title   日志标题
     * @param content 日志内容
     */
    public void recordLog(String title, String content) {
        if (ScxConfig.showLog) {
            var log = new ScxLog();
            log.userIp = NetUtils.getIpAddr();
            try {
                log.username = ScxContext.getCurrentUser(null).username;
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
