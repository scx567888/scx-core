package cool.scx.eventbus;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

/**
 * 前端发送的事件调用对象
 */
public class ScxWebSocketEvent {
    public String eventName;
    public JsonObject data;
    public String callBackID;
    public ServerWebSocket webSocket;


    public ScxWebSocketEvent(String eventName, Object data, String callBackID, ServerWebSocket webSocket) {
        this.eventName = eventName;
        this.data = JsonObject.mapFrom(data);
        this.callBackID = callBackID;
        this.webSocket = webSocket;
    }
}
