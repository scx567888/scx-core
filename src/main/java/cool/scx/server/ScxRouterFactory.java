package cool.scx.server;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.boot.ScxConfig;
import cool.scx.util.PackageUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * <p>ScxRouterFactory class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxRouterFactory {

    /**
     * <p>getRouter.</p>
     *
     * @param vertx a {@link io.vertx.core.Vertx} object.
     * @return a {@link io.vertx.ext.web.Router} object.
     */
    public static Router getRouter(Vertx vertx) {
        var router = Router.router(vertx);
        registerCorsHandler(router);
        registerBodyHandler(router);
        registerScxMappingHandler(router);
        registerStaticHandler(router);
        // 当以上所有处理器都无法匹配时 向客户端返回 404
        router.route().handler(handle -> handle.fail(404));
        return router;
    }

    /**
     * // 注册 跨域 处理器
     *
     * @param router
     */
    private static void registerCorsHandler(Router router) {
        var allowedHeaders = new HashSet<String>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");
        allowedHeaders.add(ScxConfig.tokenKey);

        var allowedMethods = new HashSet<HttpMethod>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create(ScxConfig.allowedOrigin).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).allowCredentials(true));
    }

    /**
     * // 处理 body 请求体处理器
     *
     * @param router
     */

    private static void registerBodyHandler(Router router) {
        router.post().handler(BodyHandler.create(true));
        router.put().handler(BodyHandler.create(true));
    }

    /**
     * todo
     * 处理 scxMapping 处理器
     *
     * @param router
     */
    private static void registerScxMappingHandler(Router router) {
        var a = new ArrayList<ScxMappingHandler>();
        var b = new ArrayList<ScxMappingHandler>();
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxController.class)) {
                for (var method : clazz.getMethods()) {
                    method.setAccessible(true);
                    if (method.isAnnotationPresent(ScxMapping.class)) {
                        var handler = new ScxMappingHandler(clazz, method);
                        var isRegexUrl = handler.isRegexUrl;
                        if (isRegexUrl) {
                            a.add(handler);
                        } else {
                            b.add(handler);
                        }
                    }
                }
            }
        });
        b.addAll(a);
        b.forEach(c -> {
            var route = router.route(c.url);
            c.httpMethods.forEach(route::method);
            route.blockingHandler(c);
        });
    }

    /**
     * // 静态文件 处理器
     *
     * @param router
     */
    private static void registerStaticHandler(Router router) {
        router.route(ScxConfig.cmsResourceUrl).handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(ScxConfig.cmsResourceLocations.getPath()));
    }

}
