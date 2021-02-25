package cool.scx.base.websocket;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public interface BaseWebSocketController {

    void onOpen(ServerWebSocket webSocket);

    void onClose(ServerWebSocket webSocket);

    void onError(String[] args);

    void onMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket);

    void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket);
}
