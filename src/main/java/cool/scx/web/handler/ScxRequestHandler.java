package cool.scx.web.handler;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.boot.ScxModuleHandler;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.util.Ansi;
import cool.scx.util.StringUtils;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.RouterImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
        registerFaviconHandler();
        registerCookieHandler();
        registerCorsHandler();
        registerBodyHandler();
        registerScxMappingHandler();
        registerStaticHandler();
        registerNotFoundHandler();
    }

    /**
     * 校验路由是否已经存在
     *
     * @param list    l
     * @param handler h
     * @return true 为存在 false 为不存在
     */
    private static boolean checkRouteExists(List<ScxMappingHandler> list, ScxMappingHandler handler) {
        for (var a : list) {
            if (a.url.equals(handler.url)) {
                for (var h : handler.httpMethods) {
                    if (a.httpMethods.contains(h)) {
                        Ansi.OUT.brightMagenta("检测到重复的路由!!! " + h + " --> \"" + handler.url + "\" , 相关 class 及方法如下 ▼").ln()
                                .brightMagenta(handler.clazz.getName() + " --> " + handler.method.getName()).ln()
                                .brightMagenta(a.clazz.getName() + " --> " + a.method.getName()).ln();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 注册 FaviconIco 图标 handler
     */
    private void registerFaviconHandler() {
        this.route().handler(FaviconHandler.create(ScxContext.VERTX, new File(ScxConfig.cmsRoot(), "favicon.ico").getPath()));
    }

    /**
     * 设置 session
     */
    private void registerCookieHandler() {
        this.route().handler(c -> {
            if (c.getCookie(ScxConfig.tokenKey()) == null) {
                Cookie cookie = new CookieImpl(ScxConfig.tokenKey(), StringUtils.getUUID());
                cookie.setMaxAge(60 * 60 * 24 * 7);
                c.addCookie(cookie);
            }
            c.next();
        });
    }

    /**
     * 注册 跨域 处理器
     */
    private void registerCorsHandler() {
        var allowedHeaders = Set.of("x-requested-with", "Access-Control-Allow-Origin",
                "origin", "Content-Type", "accept", "X-PINGARUNER", ScxConfig.tokenKey(), ScxConfig.deviceKey());

        var allowedMethods = Set.of(HttpMethod.GET, HttpMethod.POST,
                HttpMethod.OPTIONS, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT);

        this.route().handler(CorsHandler.create(ScxConfig.allowedOrigin()).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).allowCredentials(true));
    }

    /**
     * 处理 body 请求体处理器
     */

    private void registerBodyHandler() {
        this.route().method(HttpMethod.POST).method(HttpMethod.PUT).method(HttpMethod.DELETE).handler(new BodyHandler());
    }

    /**
     * todo
     * 处理 scxMapping 处理器
     */
    private void registerScxMappingHandler() {
        var scxMappingHandlers = new ArrayList<ScxMappingHandler>();
        ScxModuleHandler.iterateClass(clazz -> {
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
        scxMappingHandlers.stream().sorted(Comparator.comparing(s -> s.order)).forEachOrdered(c -> {
            var route = this.route(c.url);
            c.httpMethods.forEach(route::method);
            route.blockingHandler(c);
        });
    }

    /**
     * 静态文件 处理器
     */
    private void registerStaticHandler() {
        this.route(ScxConfig.cmsResourceUrl()).handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(ScxConfig.cmsResourceLocations().getPath()));
    }

    /**
     * 未匹配 处理器
     * 当以上所有处理器都无法匹配时 向客户端返回 404
     */
    private void registerNotFoundHandler() {
        this.route().handler(handle -> handle.fail(404));
    }

}
