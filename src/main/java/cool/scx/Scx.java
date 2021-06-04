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

    public static void setTimer(long pauseTime, Runnable runnable) {
        GLOBAL_VERTX.nettyEventLoopGroup().schedule(runnable, pauseTime, TimeUnit.MILLISECONDS);
    }

}
