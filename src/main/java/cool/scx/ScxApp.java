package cool.scx;

import cool.scx.auth.ScxAuth;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.dao.ScxDBContext;
import cool.scx.module.ScxModuleHandler;
import cool.scx.template.ScxTemplate;
import cool.scx.util.Timer;
import cool.scx.web.ScxRouter;
import cool.scx.web.ScxServer;

/**
 * 启动类
 *
 * @author scx567888
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
        ScxBoot.showBanner();
        // 确定 appRoot 及 appKey
        Scx.initScx(modules);
        // 初始化 配置文件
        ScxConfig.initConfig(args);
        // 初始化 上下文
        ScxContext.initContext();
        // 初始化 模板
        ScxTemplate.initTemplate();
        // 初始化 认证
        ScxAuth.initAuth();
        // 初始化 http 路由
        ScxRouter.initRouter();
        // 初始化数据库 上下文
        ScxDBContext.initDB();
        // 初始化 监听器
        ScxBoot.addListener();
        // 初始化事件总线
        ScxEventBus.initConsumer();
        // 初始化 web 服务器
        ScxServer.initServer();
        // 启动 web 服务器
        ScxServer.startServer();
        // 加载内部捆绑的 modules
        ScxModuleHandler.loadBundledModules(modules);
        // 初始化插件
        ScxModuleHandler.loadPlugins();
    }

}
