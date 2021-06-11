package cool.scx.eventbus;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

/**
 * 前端发送过来的事件封装对象
 *
 * @author 司昌旭
 * @version 1.1.17
 */
public class ScxWebSocketEvent {

    public String eventName;
    public String callBackID;
    public JsonObject data;
    public ServerWebSocket webSocket;


    /**
     * <p>Constructor for ScxWebSocketEvent.</p>
     *
     * @param eventName  a {@link java.lang.String} object
     * @param data       a {@link java.lang.Object} object
     * @param callBackID a {@link java.lang.String} object
     * @param webSocket  a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public ScxWebSocketEvent(String eventName, Object data, String callBackID, ServerWebSocket webSocket) {
        this.eventName = eventName;
        this.data = JsonObject.mapFrom(data);
        this.callBackID = callBackID;
        this.webSocket = webSocket;
    }
}
