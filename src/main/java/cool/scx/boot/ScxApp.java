package cool.scx.boot;

import cool.scx.config.ScxCmsConfig;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.gui.ScxGui;
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
     * 运行项目
     *
     * @param source 启动的 class
     * @param args   外部参数
     */
    public static void run(Class<?> source, String... args) {
        run(new Class[]{source}, args);
    }

    /**
     * 运行项目
     *
     * @param source 启动的 class
     * @param args   外部参数
     */
    private static void run(Class<?>[] source, String... args) {
        //此处每个初始化方法都依赖上一个的初始化方法 所以顺序不要打乱
        //先打印出 banner
        ScxBanner.show();
        //初始化 配置文件
        ScxConfig.initConfig(source, args);
        //初始化插件
        ScxPlugins.initPlugins();
        //初始化 cms 配置文件
        ScxCmsConfig.init();
        //初始化 context
        ScxContext.init();
        //初始化 事件监听
        ScxListener.init();
        //初始化 服务器
        ScxServer.initServer();
        //启动服务器
        ScxServer.startServer();
        //初始化 gui
        ScxGui.init();
        //初始化 license
        ScxLicense.init();
    }

}
