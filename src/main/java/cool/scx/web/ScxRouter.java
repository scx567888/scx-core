package cool.scx.web;

import cool.scx.Scx;
import cool.scx.ScxEventBus;
import cool.scx.annotation.ScxWebSocketRoute;
import cool.scx.base.BaseWSHandler;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.module.ScxModule;
import cool.scx.util.ScxUtils;
import cool.scx.util.StringUtils;
import cool.scx.web.handler.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.RouterImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>ScxRouter class.</p>
 *
 * @author scx567888
 * @version 1.1.9
 */
public final class ScxRouter {

    private static final Router vertRouter = new RouterImpl(Scx.vertx());

    private static final Map<String, BaseWSHandler> SCX_WEB_SOCKET_ROUTE_HANDLERS = new HashMap<>();

    static {
        //默认的必需路由
        vertRouter.route().order(0).handler(new FaviconHandler());
        vertRouter.route().order(1).handler(new CookieHandler());
        vertRouter.route().order(2).handler(new CorsHandler());
        vertRouter.route().order(3).method(HttpMethod.POST).method(HttpMethod.PUT).method(HttpMethod.DELETE).handler(new BodyHandler());
        //静态资源路由
        vertRouter.route(ScxConfig.templateResourceHttpUrl()).order(Integer.MAX_VALUE - 1).handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(ScxConfig.templateResourceRoot().getPath()));
        //404 匹配路由
        vertRouter.route().order(Integer.MAX_VALUE).handler(handle -> handle.fail(404));
        //Bean 加载完毕后的消费者
        ScxEventBus.consumer(ScxContext.ON_CONTEXT_REGISTER_NAME, o -> {
            var scxModuleList = ScxUtils.cast(o);
            ScxMappingHandlerRegister.register(vertRouter, scxModuleList);
            addWebSocketRouter(scxModuleList);
        });

        //Bean 销毁时的消费者
        ScxEventBus.consumer(ScxContext.ON_CONTEXT_REMOVE_NAME, scxModule -> {

        });
    }

    /**
     * <p>initRouter.</p>
     */
    public static void initRouter() {


    }

    /**
     * <p>initWebSocketRouter.</p>
     *
     * @param scxModuleList
     */
    private static void addWebSocketRouter(List<ScxModule> scxModuleList) {
        for (ScxModule scxModule : scxModuleList) {
            for (Class<?> c : scxModule.classList) {
                if (c.isAnnotationPresent(ScxWebSocketRoute.class) && !c.isInterface() && BaseWSHandler.class.isAssignableFrom(c)) {
                    var handler = (BaseWSHandler) ScxContext.getBean(c);
                    addWebSocketRoute(handler);
                }
            }
        }
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
