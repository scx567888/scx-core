package cool.scx.boot;

import cool.scx.base.BaseModule;
import cool.scx.cms.ScxCms;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.module.ScxModule;
import cool.scx.plugin.ScxPlugin;
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
     * 运行项目
     *
     * @param module 需要挂载的 module.
     * @param args   外部参数
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
        // 启动 核心计时器
        Timer.start("ScxApp");
        // 显示 banner
        ScxBanner.initBanner();
        // 装载 模块
        ScxModule.loadModules(modules);
        // 初始化 配置文件
        ScxConfig.initConfig(args);
        // 初始化 模块
        ScxModule.initModules();
        // 初始化 插件
        ScxPlugin.initPlugins();
        // 初始化 上下文
        ScxContext.initContext();
        // 初始化 cms
        ScxCms.initCms();
        // 初始化 监听器
        ScxListener.initListener();
        // 初始化 web 服务器
        ScxServer.initServer();
        // 启动 web 服务器
        ScxServer.startServer();
    }

}
