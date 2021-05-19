package cool.scx.boot;

import cool.scx.base.BaseLicenseHandler;
import cool.scx.base.BaseModule;
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

    /**
     * <p>run.</p>
     *
     * @param module a T object.
     * @param args   a {@link java.lang.String} object.
     * @param <T>    a T object.
     */
    public static <T extends BaseModule> void run(T module, String... args) {
        run(new BaseModule[]{module}, args);
    }

    /**
     * 运行项目
     *
     * @param modules 需要挂载的 module
     * @param args    外部参数
     * @param <T>     a T object.
     */
    public static <T extends BaseModule> void run(T[] modules, String... args) {
        //此处每个初始化方法都依赖上一个的初始化方法 所以顺序不要打乱
        ScxTimer.timerStart("ScxApp");
        ScxParameters.initParameters(args);
        ScxModuleHandler.initModules(modules);
        ScxBanner.show();
        ScxConfig.initConfig();
        ScxPlugins.initPlugins();
        ScxContext.initContext();
        ScxCmsConfig.initCmsConfig();
        ScxListener.initListener();
        ScxServer.initServer();
        ScxServer.startServer();
        ScxContext.getBean(BaseLicenseHandler.class).checkLicense();
    }

}
