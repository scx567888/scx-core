package cool.scx;

import cool.scx.auth.ScxAuth;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.dao.ScxDBContext;
import cool.scx.eventbus.ScxEventBus;
import cool.scx.message.ScxSender;
import cool.scx.module.ScxModuleHandler;
import cool.scx.plugin.ScxPlugin;
import cool.scx.template.ScxTemplate;
import cool.scx.util.Timer;
import cool.scx.web.ScxRouter;
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
    public static <T extends ScxModule> void run(T module, String... args) {
        run(new ScxModule[]{module}, args);
    }

    /**
     * 运行项目
     *
     * @param modules 需要挂载的 module
     * @param args    外部参数
     * @param <T>     a T object.
     */
    public static <T extends ScxModule> void run(T[] modules, String... args) {
        // 启动 核心计时器
        Timer.start("ScxApp");
        // 显示 banner
        ScxBoot.showBanner();
        // 装载 模块
        ScxModuleHandler.loadModules(modules);
        // 初始化 配置文件
        ScxConfig.initConfig(args);
        // 初始化 模块
        ScxModuleHandler.initModules();
        // 初始化 插件
        ScxPlugin.initPlugins();
        // 初始化 上下文
        ScxContext.initContext();
        // 初始化数据库 上下文
        ScxDBContext.initDB();
        // 初始化 认证
        ScxAuth.initAuth();
        // 初始化 模板
        ScxTemplate.initTemplate();
        // 初始化 消息发送模块
        ScxSender.initSender();
        // 初始化 监听器
        ScxBoot.addListener();
        // 初始化 http 路由
        ScxRouter.initRouter();
        // 初始化 websocket 路由
        ScxRouter.initWebSocketRouter();
        // 初始化事件总线
        ScxEventBus.initEventBus();
        // 初始化 web 服务器
        ScxServer.initServer();
        // 初始化 模块的 start 生命周期
        ScxModuleHandler.startModules();
        // 启动 web 服务器
        ScxServer.startServer();
    }

}
