package cool.scx;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * WebSocketController 基本接口
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public interface BaseWSHandler {

    /**
     * 连接打开时
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onOpen(ServerWebSocket webSocket);

    /**
     * 连接关闭时
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onClose(ServerWebSocket webSocket);

    /**
     * 发送 文本数据
     *
     * @param textData  a {@link java.lang.String} object.
     * @param h         a {@link io.vertx.core.http.WebSocketFrame} object.
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket);

    /**
     * 发送二进制数据
     *
     * @param binaryData a {@link io.vertx.core.buffer.Buffer} object.
     * @param h          a {@link io.vertx.core.http.WebSocketFrame} object.
     * @param webSocket  a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket);

    /**
     * 连接错误时
     *
     * @param event     a {@link java.lang.Throwable} object.
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     */
    void onError(Throwable event, ServerWebSocket webSocket);
}
