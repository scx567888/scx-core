package cool.scx.eventbus;

import cool.scx.Scx;
import cool.scx.web.ScxRouter;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * 事件总线
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public class ScxEventBus {

    /**
     * vertx 的事件总线
     */
    private static final EventBus VERTX_EVENTBUS = Scx.vertx().eventBus();

    /**
     * <p>publish.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param message a {@link java.lang.Object} object
     */
    public static void publish(String address, Object message) {
        VERTX_EVENTBUS.publish(address, message);
    }

    /**
     * <p>send.</p>
     *
     * @param var1 a {@link java.lang.String} object
     * @param var2 a {@link java.lang.Object} object
     */
    public static void send(String var1, Object var2) {
        VERTX_EVENTBUS.send(var1, var2);
    }

    /**
     * 注册消费者 这里注册的消费者 前端和本地均可以进行调用
     *
     * @param args a
     */
    public static void consumer(String[] args) {
//        VERTX_EVENTBUS.localConsumer()
    }

    /**
     * 注册本地消费者 这里注册的消费者只接受本地调用
     *
     * @param address        a {@link java.lang.String} object
     * @param messageHandler a {@link io.vertx.core.Handler} object
     * @param <T>            a T class
     * @return a {@link io.vertx.core.eventbus.MessageConsumer} object
     */
    public static <T> MessageConsumer<T> localConsumer(String address, Handler<Message<T>> messageHandler) {
        return VERTX_EVENTBUS.localConsumer(address, messageHandler);
    }

    /**
     * <p>initEventBus.</p>
     */
    public static void initEventBus() {
        ScxRouter.addWebSocketRoute(new ScxEventBusWebSocketHandler());
    }

}
