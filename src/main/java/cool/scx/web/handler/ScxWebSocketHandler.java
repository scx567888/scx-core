package cool.scx.web.handler;

import cool.scx.annotation.ScxWebSocketController;
import cool.scx.context.ScxContext;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.StringUtils;
import cool.scx.web.BaseWebSocketController;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个 handler 会获取所有的 websocket 请求并进行转发
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class ScxWebSocketHandler implements Handler<ServerWebSocket> {

    private final Map<String, BaseWebSocketController> SCX_WEB_SOCKET_CONTROLLER_HANDLERS = new HashMap<>();

    /**
     * <p>Constructor for ScxWebSocketHandler.</p>
     */
    public ScxWebSocketHandler() {
        ScxModuleHandler.iterateClass(c -> {
            if (c.isAnnotationPresent(ScxWebSocketController.class) && !c.isInterface() && BaseWebSocketController.class.isAssignableFrom(c)) {
                var annotation = c.getAnnotation(ScxWebSocketController.class);
                var path = StringUtils.clearHttpUrl(annotation.value());
                var handler = (BaseWebSocketController) ScxContext.getBean(c);
                SCX_WEB_SOCKET_CONTROLLER_HANDLERS.put(path, handler);
            }
            return true;
        });
    }

    /**
     * {@inheritDoc}
     * <p>
     * handle
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
            if (h.isText()) {
                handler.onMessage(h.textData(), h, webSocket);
            }
            if (h.isBinary()) {
                handler.onBinaryMessage(h.binaryData(), h, webSocket);
            }
        });
        webSocket.exceptionHandler(event -> handler.onError(event, webSocket));
        webSocket.closeHandler(h -> handler.onClose(webSocket));
    }

    /**
     * <p>getAllWebSocketController.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, BaseWebSocketController> getAllWebSocketController() {
        return SCX_WEB_SOCKET_CONTROLLER_HANDLERS;
    }

    /**
     * 向 handler 里添加 处理器
     * 注意!!! 必须在 ScxServer.loadServer 之前添加
     * @param args
     */
    public static void addHandler(Class<? extends BaseWebSocketController> args) {

    }

    /**
     * 向 handler 里添加 处理器
     * 注意!!! 必须在 ScxServer.loadServer 之前添加
     * @param args
     */
    public static void removeHandler(Class<? extends BaseWebSocketController> args) {

    }

    /**
     * 向 handler 里添加 处理器
     * 注意!!! 必须在 ScxServer.loadServer 之前添加
     * @param
     */
    public static void clearHandler() {

    }

    public static void initHandler() {

    }

}
