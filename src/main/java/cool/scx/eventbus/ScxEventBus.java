package cool.scx.eventbus;

import cool.scx.Scx;
import cool.scx.bo.WSBody;
import cool.scx.web.ScxRouter;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

/**
 * 事件总线<br>
 * 针对 vertx 的 eventbus 进行简单封装
 *
 * @author scx567888
 * @version 1.1.9
 */
public class ScxEventBus {

    /**
     * vertx 的事件总线
     */
    private static final EventBus VERTX_EVENTBUS = Scx.vertx().eventBus();

    /**
     * 注册消费者 这里注册的消费者 前端和本地均可以进行调用
     *
     * @param address a
     * @param handler h
     * @param <T>     t
     */
    public static <T> void consumer(String address, Handler<Message<T>> handler) {
        VERTX_EVENTBUS.localConsumer(address, handler);
    }

    /**
     * <p>wsConsumer.</p>
     *
     * @param address a
     * @param handler h
     */
    public static void wsConsumer(String address, Handler<Message<WSBody>> handler) {
        VERTX_EVENTBUS.localConsumer(address, handler);
    }

    /**
     * <p>initEventBus.</p>
     */
    public static void initEventBus() {
        ScxRouter.addWebSocketRoute(new ScxEventBusWebSocketHandler());
        VERTX_EVENTBUS.registerDefaultCodec(WSBody.class, new WSBodyCodec());
        initDefaultHandler();
    }

    /**
     * <p>requestScxWebSocketEvent.</p>
     *
     * @param WSBody a {@link cool.scx.bo.WSBody} object
     */
    public static void requestScxWebSocketEvent(WSBody WSBody) {
        VERTX_EVENTBUS.request(WSBody.eventName(), WSBody);
    }

    /**
     * <p>vertxEventbus.</p>
     *
     * @return a {@link io.vertx.core.eventbus.EventBus} object
     */
    public static EventBus vertxEventbus() {
        return VERTX_EVENTBUS;
    }

    /**
     * 初始化默认 handler
     */
    private static void initDefaultHandler() {
        ScxEventBus.wsConsumer("auth-token", (Message<WSBody> m) -> AuthLoginHandler.loginByWebSocket(m.body()));
    }

}
