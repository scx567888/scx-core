package cool.scx.web;

import cool.scx.Scx;
import cool.scx.annotation.ScxWebSocketRoute;
import cool.scx.base.BaseWSHandler;
import cool.scx.context.ScxContext;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.StringUtils;
import cool.scx.web.handler.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.impl.RouterImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>ScxRouter class.</p>
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public final class ScxRouter {

    private static final Router vertRouter = new RouterImpl(Scx.vertx());

    private static final Map<String, BaseWSHandler> SCX_WEB_SOCKET_ROUTE_HANDLERS = new HashMap<>();

    /**
     * <p>initRouter.</p>
     */
    public static void initRouter() {
        vertRouter.route().handler(new FaviconHandler());
        vertRouter.route().handler(new CookieHandler());
        vertRouter.route().handler(new CorsHandler());
        vertRouter.route().method(HttpMethod.POST).method(HttpMethod.PUT).method(HttpMethod.DELETE).handler(new BodyHandler());
        ScxMappingHandlerRegister.register(vertRouter);
        StaticHandlerRegister.register(vertRouter);
        vertRouter.route().handler(handle -> handle.fail(404));
    }

    /**
     * <p>initWebSocketRouter.</p>
     */
    public static void initWebSocketRouter() {
        ScxModuleHandler.iterateClass(c -> {
            if (c.isAnnotationPresent(ScxWebSocketRoute.class) && !c.isInterface() && BaseWSHandler.class.isAssignableFrom(c)) {
                var handler = (BaseWSHandler) ScxContext.getBean(c);
                addWebSocketRoute(handler);
            }
            return true;
        });
    }

    /**
     * <p>handle.</p>
     *
     * @param request a {@link io.vertx.core.http.HttpServerRequest} object
     */
    public static void handle(HttpServerRequest request) {
        vertRouter.handle(request);
    }

    /**
     * <p>webSocketHandler.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public static void webSocketHandler(ServerWebSocket webSocket) {
        var handler = SCX_WEB_SOCKET_ROUTE_HANDLERS.get(webSocket.path());
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
     * <p>routeSize.</p>
     *
     * @return a int
     */
    public static int routeSize() {
        return vertRouter.getRoutes().size();
    }

    /**
     * <p>webSocketRouteSize.</p>
     *
     * @return a int
     */
    public static int webSocketRouteSize() {
        return SCX_WEB_SOCKET_ROUTE_HANDLERS.size();
    }

    /**
     * <p>addWebSocketRoute.</p>
     *
     * @param handler a T object
     * @param <T>     a T class
     */
    public static <T extends BaseWSHandler> void addWebSocketRoute(T handler) {
        var annotation = handler.getClass().getAnnotation(ScxWebSocketRoute.class);
        if (annotation != null) {
            var path = StringUtils.clearHttpUrl(annotation.value());
            SCX_WEB_SOCKET_ROUTE_HANDLERS.put(path, handler);
        }
    }
}
