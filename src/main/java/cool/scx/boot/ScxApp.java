package cool.scx.boot;

import cool.scx.base.BaseModule;
import cool.scx.config.ScxCmsConfig;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.plugin.ScxPlugins;
import cool.scx.util.Ansi;
import cool.scx.util.NetUtils;
import cool.scx.util.Timer;
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
     * @param <T>    BaseModule
     * @param args   a {@link java.lang.String} object.
     */
    public static <T extends BaseModule> void run(T module, String... args) {
        run(new BaseModule[]{module}, args);
    }

    /**
     * 运行项目
     *
     * @param modules 需要挂载的 module
     * @param <T>     BaseModule
     * @param args    外部参数
     */
    public static <T extends BaseModule> void run(T[] modules, String... args) {
        //此处每个初始化方法都依赖上一个的初始化方法 所以顺序不要打乱
        ScxApp.initStartComplete();
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
    }

    private static void initStartComplete() {
        Timer.start("ScxApp");
        ScxVertx.eventBus().consumer("startVertxServer", (message) -> {
            var port = message.body().toString();
            Ansi.OUT.green("服务器启动成功... 用时 " + Timer.stopToMillis("ScxApp") + " ms").ln();
            var httpOrHttps = ScxConfig.openHttps() ? "https" : "http";
            Ansi.OUT.green("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + port + "/").ln();
            Ansi.OUT.green("> 本地 : " + httpOrHttps + "://localhost:" + port + "/").ln();
        });
    }

}
