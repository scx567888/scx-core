package cool.scx;

import cool.scx.util.Ansi;
import cool.scx.util.ScanClassUtils;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * Scx 核心类
 *
 * @author scx567888
 * @version 1.1.9
 */
public final class Scx {

    /**
     * 全局 vert.x
     */
    private static final Vertx GLOBAL_VERTX = initGlobalVertx();

    /**
     * 项目根模块 所在路径
     * 默认取 所有自定义模块的最后一个 所在的文件根目录
     */
    private static File APP_ROOT;

    /**
     * 默认的核心包 APP KEY (密码) , 注意请不要在您自己的模块中使用此常量 , 非常不安全
     */
    private static final String DEFAULT_APP_KEY = "SCX-123456";

    /**
     * 项目的 appKey
     * 默认取 所有自定义模块的最后一个的AppKey
     */
    private static String APP_KEY = DEFAULT_APP_KEY;

    /**
     * 创建全局 vert.x
     *
     * @return v
     */
    private static Vertx initGlobalVertx() {
        var globalVertxOptions = new VertxOptions();
        return Vertx.vertx(globalVertxOptions);
    }

    /**
     * 获取全局的 vertx
     *
     * @return 全局的事件总线
     */
    public static Vertx vertx() {
        return GLOBAL_VERTX;
    }

    /**
     * 设置计时器
     * <p>
     * 本质上时内部调用 netty 的线程池完成
     * <p>
     * 因为java无法做到特别精确的计时所以此处单位采取 毫秒
     *
     * @param pauseTime 延时执行的时间  单位毫秒
     * @param runnable  执行的事件
     */
    public static void setTimer(long pauseTime, Runnable runnable) {
        GLOBAL_VERTX.nettyEventLoopGroup().schedule(runnable, pauseTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行一个事件
     *
     * @param command 事件
     */
    public static void execute(Runnable command) {
        GLOBAL_VERTX.nettyEventLoopGroup().execute(command);
    }

    protected static <T extends BaseModule> void initScx(T[] modules) {
        var lastModules = modules[modules.length - 1];
        initAppRoot(lastModules);
        initAppKey(lastModules);
    }

    /**
     * 确定 appRoot
     *
     * @param modules mo
     * @param <T>     t
     */
    private static <T extends BaseModule> void initAppRoot(T appModule) {
        try {
            var classSourceFile = new File(ScanClassUtils.getClassSource(appModule.getClass()));
            //判断当前是否处于 jar 包中
            var inJar = !classSourceFile.isDirectory() && classSourceFile.getPath().endsWith(".jar");
            APP_ROOT = inJar ? classSourceFile.getParentFile() : classSourceFile;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Ansi.OUT.red("获取 AppRoot 失败!!!");
        }

    }

    private static <T extends BaseModule> void initAppKey(T appModule) {
        if (appModule.appKey() != null) {
            APP_KEY = appModule.appKey();
        }
        if (DEFAULT_APP_KEY.equals(APP_KEY)) {
            Ansi.OUT.red("注意!!! 检测到使用了默认的 DEFAULT_APP_KEY , 这是非常不安全的 , 建议重写自定义模块的 appKey() 方法以设置自定义的 APP_KEY !!!").ln();
        }
    }

    /**
     * <p>appRootPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File appRoot() {
        return APP_ROOT;
    }

    /**
     * 获取 appKey.
     *
     * @return a {@link java.lang.String} object
     */
    public static String appKey() {
        return APP_KEY;
    }

}
