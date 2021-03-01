package cool.scx.boot;

import cool.scx.ScxCoreApp;
import cool.scx.config.ScxCmsConfig;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.server.ScxServer;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * 启动类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxApp {

    private static ScxAppExample sae;

    /**
     * 运行项目
     *
     * @param source 启动的 class
     * @param args   外部参数
     */
    public static void run(Class<?> source, String... args) {
        sae = new ScxAppExample(args, source);
        run();
    }

    /**
     * 运行项目
     *
     * @param source 启动的 class
     * @param args   外部参数
     */
    public static void run(Class<?>[] source, String... args) {
        sae = new ScxAppExample(args, source);
        run();
    }

    /**
     * 当 sae 初始化成功时调用此方法
     */
    private static void run() {
        //先打印出 banner
        ScxBanner.show();
        //初始化 配置文件
        ScxConfig.init();
        //初始化插件
        ScxPlugins.init();
        //初始化 cms 配置文件
        ScxCmsConfig.init();
        //初始化 context
        ScxContext.init();
        //初始化 事件监听
        ScxListener.init();
        //初始化 服务器
        ScxServer.init();
        //初始化 gui
        ScxGui.init();
        //初始化 license
        ScxLicense.init();
    }

    /**
     * 在 classSource 中寻找 程序的 主运行 class
     * 后续会以此 以确定 程序运行的路径
     * 并以此为标准获取 配置文件 等
     *
     * @return a {@link java.lang.Class} object.
     */
    public static Class<?> getAppClassSources() {
        //因为 classSources 第一位永远是 ScxCoreApp 所以做此处理
        return sae.classSources.length == 1 ? sae.classSources[0] : sae.classSources[1];
    }

    /**
     * 获取 从外部传来的参数 (java -jar scx.jar  xxx)
     *
     * @return 外部传来的参数
     */
    public static String[] parameters() {
        return sae.parameters;
    }

    public static Class<?>[] classSources() {
        return sae.classSources;
    }

    private static class ScxAppExample {
        private final String[] parameters;
        /**
         * 整个 启动的class 源 用来进行注解扫描和依赖注入扫描
         * <p>
         * 这里我们默认 添加 scxCoreApp 用来扫描核心 业务 如 登录 文件上传等
         * 后期可能会将 核心业务拆分成 独立的 项目
         */
        private final Class<?>[] classSources;

        /**
         * 初始化 classSources 和 args
         * 主要是 去重 和 添加 默认 class Sources
         *
         * @param classSources 待处理的 classSources
         */
        public ScxAppExample(String[] parameters, Class<?>... classSources) {
            this.parameters = parameters;
            //利用 set 进行 过滤
            //以保证 参数都是未重复的
            var tempSet = new LinkedHashSet<Class<?>>();
            tempSet.add(ScxCoreApp.class);
            tempSet.addAll(Arrays.asList(classSources));
            //返回处理后的数组
            this.classSources = tempSet.toArray(Class<?>[]::new);
        }
    }
}
