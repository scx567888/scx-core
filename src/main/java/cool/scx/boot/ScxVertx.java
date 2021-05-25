package cool.scx.boot;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

/**
 * ScxVertx
 *
 * @author 司昌旭
 * @version 1.1.1
 */
public final class ScxVertx {

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
     * 获取全局的事件总线
     *
     * @return 全局的事件总线
     */
    public static EventBus eventBus() {
        return GLOBAL_VERTX.eventBus();
    }

    /**
     * 获取全局的 vertx
     *
     * @return 全局的事件总线
     */
    public static Vertx vertx() {
        return GLOBAL_VERTX;
    }

}
