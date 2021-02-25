package cool.scx.server.handler;

import cool.scx.annotation.websocket.ScxWebSocketController;
import cool.scx.base.websocket.BaseWebSocketController;
import cool.scx.boot.ScxContext;
import cool.scx.enumeration.ScanPackageVisitResult;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个 handler 会获取所有的 websocket 请求并进行转发
 *
 * @author scx56
 * @version $Id: $Id
 */
public class ScxWebSocketHandler implements Handler<ServerWebSocket> {

    private final Map<String, BaseWebSocketController> SCX_WEB_SOCKET_CONTROLLER_HANDLERS = new HashMap<>();

    /**
     * <p>Constructor for ScxWebSocketHandler.</p>
     */
    public ScxWebSocketHandler() {
        PackageUtils.scanPackage(c -> {
            if (c.isAnnotationPresent(ScxWebSocketController.class) && !c.isInterface() && BaseWebSocketController.class.isAssignableFrom(c)) {
                var annotation = c.getAnnotation(ScxWebSocketController.class);
                var path = StringUtils.clearHttpUrl(annotation.value());
                var handler = (BaseWebSocketController) ScxContext.getBean(c);
                SCX_WEB_SOCKET_CONTROLLER_HANDLERS.put(path, handler);
            }
            return ScanPackageVisitResult.CONTINUE;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(ServerWebSocket webSocket) {
        var handler = SCX_WEB_SOCKET_CONTROLLER_HANDLERS.get(webSocket.path());
        if (handler == null) {
            webSocket.close();
            return;
        }
        handler.onOpen(webSocket);
        //　WebSocket 连接
        webSocket.frameHandler(h -> {
            var textData = h.textData();
            if (textData != null) {
                handler.onMessage(textData, h, webSocket);
            }
            var binaryData = h.binaryData();
            if (binaryData != null) {
                handler.onBinaryMessage(binaryData, h, webSocket);
            }
        });
        webSocket.closeHandler(h -> handler.onClose(webSocket));
    }

}
