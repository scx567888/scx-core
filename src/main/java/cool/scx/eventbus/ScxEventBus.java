package cool.scx.eventbus;

import cool.scx.Scx;
import cool.scx.eventbus.handler.LoginByWebSocketHandler;
import cool.scx.eventbus.handler.SendMessageByWebsocketHandler;
import cool.scx.util.ObjectUtils;
import cool.scx.web.ScxRouter;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

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
     * 注册消费者 这里注册的消费者 前端和本地均可以进行调用
     *
     * @param address a
     * @param handler h
     * @param <T>     t
     */
    public static <T> void consumer(String address, Handler<Message<T>> handler) {
        VERTX_EVENTBUS.consumer(address, handler);
    }

    /**
     * <p>initEventBus.</p>
     */
    public static void initEventBus() {
        ScxRouter.addWebSocketRoute(new ScxEventBusWebSocketHandler());
        initDefaultHandler();
    }

    /**
     * <p>requestScxWebSocketEvent.</p>
     *
     * @param event a {@link cool.scx.eventbus.ScxWebSocketEvent} object
     */
    public static void requestScxWebSocketEvent(ScxWebSocketEvent event) {
        VERTX_EVENTBUS.request(event.eventName, event.data, (c) -> {
            if (c.succeeded()) {
                Object body = c.result().body();
                var result = new ScxWebSocketEventResult(event.callBackID, body);
                var message = ObjectUtils.beanToJsonUseAnnotations(result);
                event.webSocket.writeTextMessage(message);
            } else {
                Throwable cause = c.cause();
                cause.printStackTrace();
            }
        });
    }

    /**
     * <p>vertxEventbus.</p>
     *
     * @return a {@link io.vertx.core.eventbus.EventBus} object
     */
    public static EventBus vertxEventbus() {
        return VERTX_EVENTBUS;
    }

    private static void initDefaultHandler() {
        consumer("login", (Message<JsonObject> m) -> {
            LoginByWebSocketHandler.loginByWebSocket(m.body());
        });
        consumer("hello", (Message<JsonObject> m) -> {
            String s = SendMessageByWebsocketHandler.sendMessage(m.body());
            m.reply(s);
        });
    }

}
