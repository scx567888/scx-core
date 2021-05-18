package cool.scx.boot;

import cool.scx.config.ScxCmsConfig;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.plugin.ScxPlugins;
import cool.scx.web.ScxServer;

/**
 * 启动类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxApp {

    public static <T extends ScxModule> void run(T source, String... args) {
        run(new ScxModule[]{source}, args);
    }

    /**
     * 运行项目
     *
     * @param source 启动的 class
     * @param args   外部参数
     */
    public static <T extends ScxModule> void run(T[] source, String... args) {
        //此处每个初始化方法都依赖上一个的初始化方法 所以顺序不要打乱
        ScxTimer.timerStart();
        ScxParameters.initParameters(source, args);
        ScxBanner.show();
        ScxConfig.initConfig();
        ScxPlugins.initPlugins();
        ScxCmsConfig.initCmsConfig();
        ScxContext.initContext();
        ScxListener.initListener();
        ScxServer.initServer();
        ScxServer.startServer();
        ScxContext.getBean(ScxLicense.class).checkLicense();
    }

}
