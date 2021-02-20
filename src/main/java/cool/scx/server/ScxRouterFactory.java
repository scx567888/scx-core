package cool.scx.server;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.BaseVo;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.business.user.User;
import cool.scx.exception.HttpResponseException;
import cool.scx.util.ObjectUtils;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
        // 注册 跨域 处理器
        registerCorsHandler(router);
        // 处理 body 请求体处理器
        registerBodyHandler(router);
        // 处理 scxMapping 处理器 此处校验是否拥有权限
        registerScxMappingHandler(router);
        // 静态文件 处理器
        registerStaticHandler(router);
        // 当以上所有处理器都无法匹配时 向客户端返回 404
        router.route().handler(handle -> handle.fail(404));
        return router;
    }

    private static void checkedLogin(Router router, String url) {
        var s = new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext ctx) {
                User currentUser = ScxContext.getCurrentUser(ctx);
                if (currentUser == null) {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json; charset=utf-8");
                    response.end(Json.fail(Json.ILLEGAL_TOKEN, "未登录").getBuffer());
                } else {
                    ctx.next();
                }
            }
        };
        router.route(url).handler(s);
    }

    /**
     * todo
     *
     * @param router
     * @param url
     */
    private static void checkedPerms(Router router, String url) {

//        ctx.next();

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

        router.route().handler(CorsHandler.create(ScxConfig.allowedOrigin).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).allowCredentials(true));
    }

    private static void registerBodyHandler(Router router) {
        router.post().handler(BodyHandler.create(true));
        router.put().handler(BodyHandler.create(true));
    }

    /**
     * todo
     *
     * @param router
     */
    private static void registerScxMappingHandler(Router router) {
        var a = new ArrayList<TempRoute>();
        var b = new ArrayList<TempRoute>();
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            var scxController = clazz.getAnnotation(ScxController.class);
            if (scxController != null) {
                for (Method method : clazz.getMethods()) {
                    method.setAccessible(true);
                    ScxMapping scxMapping = method.getAnnotation(ScxMapping.class);
                    if (scxMapping != null) {
                        var url = scxMapping.useMethodNameAsUrl() && "".equals(scxMapping.value()) ? StringUtils.clearHttpUrl("api", StringUtils.getApiNameByControllerName(clazz), method.getName())
                                : StringUtils.clearHttpUrl(scxController.value(), scxMapping.value());
                        if (url.contains(":") || url.contains("*")) {
                            a.add(new TempRoute(url, method, clazz, scxMapping));
                        } else {
                            b.add(new TempRoute(url, method, clazz, scxMapping));
                        }
                    }
                }
            }
        });
        b.addAll(a);
        b.forEach(b1 -> {
            var scxMapping = b1.scxMapping;
            var url = b1.url;
            var method = b1.method;
            var clazz = b1.clazz;
            //    不校验任何东西
            if (scxMapping.unCheckedLogin()) {
                Arrays.asList(scxMapping.httpMethod()).forEach(httpMethod ->
                        router.route(HttpMethod.valueOf(httpMethod.toString()), url)
                                .blockingHandler(ctx -> callHandler(ctx, new ScxRouteHandler(method, ScxContext.getBean(clazz), scxMapping))));
            }
            //    校验登录 但不校验权限
            else if (scxMapping.unCheckedPerms()) {
                checkedLogin(router, url);
                Arrays.asList(scxMapping.httpMethod()).forEach(httpMethod ->
                        router.route(HttpMethod.valueOf(httpMethod.toString()), url)
                                .blockingHandler(ctx -> callHandler(ctx, new ScxRouteHandler(method, ScxContext.getBean(clazz), scxMapping))));
            }
            //校验登录和权限
            else {
                checkedLogin(router, url);
                checkedPerms(router, url);
                Arrays.asList(scxMapping.httpMethod()).forEach(httpMethod ->
                        router.route(HttpMethod.valueOf(httpMethod.toString()), url)
                                .blockingHandler(ctx -> callHandler(ctx, new ScxRouteHandler(method, ScxContext.getBean(clazz), scxMapping))));
            }
        });

    }

    private static void callHandler(RoutingContext ctx, ScxRouteHandler scxRouteHandler) {
        var response = ctx.response();
        Object result;
        try {
            result = scxRouteHandler.getResult(ctx);
        } catch (Exception e) {
            var cause = e.getCause();
            // 我们后面会自定义一些其他 自定义异常
            //在此处进行截获处理
            if (cause instanceof HttpResponseException) {
                ((HttpResponseException) cause).errFun.accept(ctx);
                ctx.end();
                return;
            }
            response.end(Json.fail(Json.SYSTEM_ERROR, e.getMessage()).getBuffer());
            e.printStackTrace();
            return;
        }
        if (result instanceof String || result instanceof Integer || result instanceof Double || result instanceof Boolean) {
            response.putHeader("Content-Type", "text/plain; charset=utf-8");
            response.end(result.toString());
            return;
        }
        if (result instanceof BaseVo) {
            BaseVo baseVo = (BaseVo) result;
            response.putHeader("Content-Type", baseVo.getContentType());
            String contentDisposition = baseVo.getContentDisposition();
            if (StringUtils.isNotEmpty(contentDisposition)) {
                response.putHeader("Content-Disposition", contentDisposition);
            }
            response.end(baseVo.getBuffer());
            return;
        }
        response.end(ObjectUtils.beanToJson(result));
    }

    private static void registerStaticHandler(Router router) {
        router.route(ScxConfig.cmsResourceUrl).handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(ScxConfig.cmsResourceLocations.getPath()));
    }

}
