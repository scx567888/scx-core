package cool.scx.eventbus;

import cool.scx.Scx;
import cool.scx.bo.WSBody;
import cool.scx.handler.DefaultHandler;
import cool.scx.util.ObjectUtils;
import cool.scx.web.ScxRouter;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * 事件总线<br>
 * 针对 vertx 的 eventbus 进行简单封装
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
        DefaultHandler.initDefaultHandler();
    }

    /**
     * <p>requestScxWebSocketEvent.</p>
     *
     * @param scxWSBody a {@link ScxWSBody} object
     */
    public static void requestScxWebSocketEvent(ScxWSBody scxWSBody) {
        VERTX_EVENTBUS.request(scxWSBody.wsBody.eventName,
                JsonObject.mapFrom(scxWSBody.wsBody.data),
                (c) -> {
                    if (c.succeeded()) {
                        Object body = c.result().body();
                        var result = new WSBody(null, scxWSBody.wsBody.callBackID, body);
                        var message = ObjectUtils.beanToJsonUseAnnotations(result);
                        scxWSBody.webSocket.writeTextMessage(message);
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

}
