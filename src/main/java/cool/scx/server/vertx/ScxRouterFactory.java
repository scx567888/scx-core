package cool.scx.server.vertx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Html;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Arrays;
import java.util.HashSet;

public final class ScxRouterFactory {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Router getRouter(Vertx vertx) {
        var router = Router.router(vertx);
        registerCorsHandler(router);
        registerBodyHandler(router);
        registerScxMappingHandler(router);
        registerStaticHandler(router);
        return router;
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
        router.post().order(0).handler(BodyHandler.create(true));
        router.put().order(0).handler(BodyHandler.create(true));
    }

    private static void registerScxMappingHandler(Router router) {
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxService.class)) {
                var scxService = clazz.getAnnotation(ScxService.class);
                Arrays.stream(clazz.getMethods()).filter(method -> method.isAnnotationPresent(ScxMapping.class) && !Arrays.asList(scxService.excludeMethod()).contains(method.getName())).forEach(method -> {
                    method.setAccessible(true);
                    var scxMapping = method.getAnnotation(ScxMapping.class);
                    var url = scxMapping.useMethodNameAsUrl() ? StringUtils.cleanHttpUrl("api", StringUtils.toLowerCaseFirstOne(clazz.getSimpleName()), method.getName())
                            : StringUtils.cleanHttpUrl(scxService.value(), scxMapping.value());
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
        try {
            return OBJECT_MAPPER.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
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
            default:
        }
        response.putHeader("content-type", contentType);
    }

    private static void registerStaticHandler(Router router) {
        router.route(ScxConfig.cmsResourceUrl).handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(ScxConfig.cmsResourceLocations.getPath()));
    }

}
