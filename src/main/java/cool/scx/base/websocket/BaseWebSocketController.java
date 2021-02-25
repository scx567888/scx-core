package cool.scx.base.websocket;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * <p>BaseWebSocketController interface.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public interface BaseWebSocketController {

    /**
     * <p>onOpen.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onOpen(ServerWebSocket webSocket);

    /**
     * <p>onClose.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onClose(ServerWebSocket webSocket);

    /**
     * <p>onError.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    void onError(String[] args);

    /**
     * <p>onMessage.</p>
     *
     * @param textData a {@link java.lang.String} object.
     * @param h a {@link io.vertx.core.http.WebSocketFrame} object.
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket);

    /**
     * <p>onBinaryMessage.</p>
     *
     * @param binaryData a {@link io.vertx.core.buffer.Buffer} object.
     * @param h a {@link io.vertx.core.http.WebSocketFrame} object.
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket);
}
