package cool.scx.business.notice;

import cool.scx.annotation.websocket.ScxWebSocketController;
import cool.scx.base.websocket.BaseWebSocketController;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * <p>NoticeController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxWebSocketController("/scx")
public class NoticeController implements BaseWebSocketController {


    @Override
    public void onOpen(ServerWebSocket webSocket) {
        System.out.println(webSocket);
        System.out.println("fnvksjfnksdjfnvksfnvksdjfvnkn");
    }

    @Override
    public void onClose(ServerWebSocket webSocket) {
        System.out.println(webSocket);
    }

    @Override
    public void onError(String[] args) {

    }

    @Override
    public void onMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket) {
        System.out.println(textData);
        webSocket.writeTextMessage("司昌旭");
    }

    @Override
    public void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket) {
//        System.out.println(binaryData);
    }
}
