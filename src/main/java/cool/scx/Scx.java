package cool.scx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.concurrent.TimeUnit;

/**
 * Scx 核心类
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public final class Scx {

    /**
     * 全局 vert.x
     */
    private static final Vertx GLOBAL_VERTX = initGlobalVertx();

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

}
