package cool.scx.web.handler;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.RouterImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>ScxRouterFactory class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxRequestHandler extends RouterImpl {

    /**
     * <p>Constructor for ScxRequestHandler.</p>
     */
    public ScxRequestHandler() {
        super(ScxContext.VERTX);
        registerFaviconHandler(this);
        registerCookieHandler(this);
        registerCorsHandler(this);
        registerBodyHandler(this);
        registerScxMappingHandler(this);
        registerStaticHandler(this);
        // 当以上所有处理器都无法匹配时 向客户端返回 404
        this.route().handler(handle -> handle.fail(404));
    }

    /**
     * 注册 FaviconIco 图标 handler
     *
     * @param router
     */
    private static void registerFaviconHandler(Router router) {
        router.route().handler(FaviconHandler.create(ScxContext.VERTX, ScxConfig.cmsFaviconIcoPath().getPath()));
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
        allowedHeaders.add(ScxConfig.tokenKey());

        var allowedMethods = new HashSet<HttpMethod>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create(ScxConfig.allowedOrigin()).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).allowCredentials(true));
    }

    /**
     * // 处理 body 请求体处理器
     *
     * @param router
     */

    private static void registerBodyHandler(Router router) {
        router.route().method(HttpMethod.POST).method(HttpMethod.PUT).handler(new BodyHandler());
    }

    /**
     * todo
     * 处理 scxMapping 处理器
     *
     * @param router
     */
    private static void registerScxMappingHandler(Router router) {
        var scxMappingHandlers = new ArrayList<ScxMappingHandler>();
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxController.class)) {
                for (var method : clazz.getMethods()) {
                    method.setAccessible(true);
                    if (method.isAnnotationPresent(ScxMapping.class)) {
                        //现根据 注解 和 方法等创建一个路由
                        var s = new ScxMappingHandler(clazz, method);
                        //此处校验路由是否已经存在
                        var b = checkRouteExists(scxMappingHandlers, s);
                        if (!b) {
                            scxMappingHandlers.add(s);
                        }
                    }
                }
            }
            return true;
        });
        //此处排序的意义在于将 需要正则表达式匹配的 放在最后 防止匹配错误
        var orderedScxMappingHandlers = scxMappingHandlers.stream().sorted(Comparator.comparing(s -> s.order)).collect(Collectors.toList());
        orderedScxMappingHandlers.forEach(c -> {
            var route = router.route(c.url);
            c.httpMethods.forEach(route::method);
            route.blockingHandler(c);
        });
    }

    /**
     * 校验路由是否已经存在
     *
     * @param list    l
     * @param handler h
     * @return true 为存在 false 为不存在
     */
    private static boolean checkRouteExists(List<ScxMappingHandler> list, ScxMappingHandler handler) {
        for (var httpMethod : handler.httpMethods) {
            var d = list.stream().filter(a -> a.url.equals(handler.url) && a.httpMethods.contains(httpMethod)).findAny().orElse(null);
            if (d != null) {
                Ansi.OUT.brightMagenta("检测到重复的路由!!! " + httpMethod + " --> \"" + handler.url + "\" , 相关 class 及方法如下 ▼").ln()
                        .brightMagenta(handler.clazz.getName() + " --> " + handler.method.getName()).ln()
                        .brightMagenta(d.clazz.getName() + " --> " + d.method.getName()).ln();
                return true;
            }
        }
        return false;
    }

    /**
     * // 静态文件 处理器
     *
     * @param router
     */
    private static void registerStaticHandler(Router router) {
        router.route(ScxConfig.cmsResourceUrl()).handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(ScxConfig.cmsResourceLocations().getPath()));
    }

    /**
     * 设置 session
     *
     * @param router
     */
    private static void registerCookieHandler(Router router) {
        router.route().handler(c -> {
            if (c.getCookie(ScxConfig.cookieKey()) == null) {
                Cookie cookie = new CookieImpl(ScxConfig.cookieKey(), StringUtils.getUUID());
                c.addCookie(cookie);
            }
            c.next();
        });
    }

}
