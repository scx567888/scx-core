package cool.scx.server;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.util.ObjectUtils;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Html;
import cool.scx.vo.Json;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.CookieSameSite;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.util.Arrays;
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
        //  注册 session 处理器
        registerSessionHandler(router, vertx);
        // 注册 跨域 处理器
        registerCorsHandler(router);
        // 处理 body 请求体处理器
        registerBodyHandler(router);
        // 处理 scx 权限处理器 此处校验是否登录
        registerScxPermsHandler(router);
        // 处理 scxMapping 处理器 此处校验是否拥有权限
        registerScxMappingHandler(router);
        // 静态文件 处理器
        registerStaticHandler(router);
        // 异常处理器
        router.route().failureHandler(ErrorHandler.create(vertx));
        // 当以上所有处理器都无法匹配时 向客户端返回 404
        router.route().handler(handle -> handle.fail(404));
        return router;
    }

    private static void registerScxPermsHandler(Router router) {
        var excludeCheckPermsUrls = Arrays.asList(ScxConfig.excludeCheckPermsUrls);
        var s = new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext ctx) {
                String url = ctx.request().uri().split("\\?")[0];
                if (!excludeCheckPermsUrls.contains(url) && ctx.session().get(ScxConfig.tokenKey) == null) {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json; charset=utf-8");
                    response.end(ObjectUtils.beanToJson(Json.fail(Json.ILLEGAL_TOKEN, "未登录")));
                } else {
                    ctx.next();
                }
            }
        };
        for (String checkPermsUrl : ScxConfig.checkPermsUrls) {
            router.post(checkPermsUrl).order(0).handler(s);
            router.put(checkPermsUrl).order(0).handler(s);
            router.delete(checkPermsUrl).order(0).handler(s);
            router.get(checkPermsUrl).order(0).handler(s);
        }

    }

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

        router.route().order(0).handler(CorsHandler.create(ScxConfig.allowedOrigin).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).allowCredentials(true));
    }

    private static void registerBodyHandler(Router router) {
        router.post().order(0).handler(BodyHandler.create(true));
        router.put().order(0).handler(BodyHandler.create(true));
    }

    private static void registerScxMappingHandler(Router router) {
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxController.class)) {
                var scxController = clazz.getAnnotation(ScxController.class);
                Arrays.stream(clazz.getMethods()).filter(method -> method.isAnnotationPresent(ScxMapping.class)).forEach(method -> {
                    method.setAccessible(true);
                    var scxMapping = method.getAnnotation(ScxMapping.class);
                    var url = scxMapping.useMethodNameAsUrl() && "".equals(scxMapping.value()) ? StringUtils.cleanHttpUrl("api", StringUtils.getModelNameByControllerName(clazz.getSimpleName()), method.getName())
                            : StringUtils.cleanHttpUrl(scxController.value(), scxMapping.value());
                    Arrays.asList(scxMapping.httpMethod()).forEach(httpMethod ->
                            router.route(HttpMethod.valueOf(httpMethod.toString()), url)
                                    .order((url.contains(":") || url.contains("*")) ? 2 : 1)
                                    .blockingHandler(ctx -> callHandler(ctx, new ScxRouteHandler(method, ScxContext.getBean(clazz), scxMapping))));
                });
            }
        });
    }

    private static void callHandler(RoutingContext ctx, ScxRouteHandler scxRouteHandler) {
        var response = ctx.response();
        fillContentType(response, scxRouteHandler);
        response.end(getStringFormObject(scxRouteHandler.getResult(ctx)));
    }

    private static String getStringFormObject(Object result) {
        var aClass = result.getClass();
        if (aClass == String.class) {
            return result.toString();
        }
        if (aClass == Html.class) {
            return ((Html) result).getHtmlStr();
        }
        return ObjectUtils.beanToJson(result);
    }

    private static void fillContentType(HttpServerResponse response, ScxRouteHandler scxRouteHandler) {
        var contentType = "text/plain";
        switch (scxRouteHandler.scxMapping.returnType()) {
            case JSON:
                contentType = "application/json; charset=utf-8";
                break;
            case HTML:
                contentType = "text/html; charset=utf-8";
                break;
            case AUTO:
                Class<?> returnType = scxRouteHandler.method.getReturnType();
                if (returnType == Html.class) {
                    contentType = "text/html; charset=utf-8";
                } else if (returnType == Json.class) {
                    contentType = "application/json; charset=utf-8";
                } else {
                    contentType = "application/json; charset=utf-8";
                }
                break;
            default:
        }
        response.putHeader("content-type", contentType);
    }

    private static void registerStaticHandler(Router router) {
        router.route(ScxConfig.cmsResourceUrl).handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(ScxConfig.cmsResourceLocations.getPath()));
    }

    private static void registerSessionHandler(Router router, Vertx vertx) {
        var sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
        sessionHandler.setCookieSameSite(CookieSameSite.NONE);
        //设置为 true 表示只允许 https
        sessionHandler.setCookieSecureFlag(true);
        sessionHandler.setCookieHttpOnlyFlag(true);
        sessionHandler.setSessionCookieName(ScxConfig.sessionCookieName);
        router.route().order(0).handler(sessionHandler);
    }

}
