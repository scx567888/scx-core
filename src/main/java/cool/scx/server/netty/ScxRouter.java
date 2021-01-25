package cool.scx.server.netty;

import cool.scx.annotation.ScxMapping;
import cool.scx.annotation.ScxService;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class ScxRouter {

    private static final Map<String, ScxRouteHandler> router = new HashMap<>();

    static {
        registerScxMappingHandler();
    }


    private static void registerCorsHandler(FullHttpResponse fullHttpResponse) {
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

//        router.route().handler(CorsHandler.create(ScxConfig.allowedOrigin).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).allowCredentials(true));
    }


    private static void registerScxMappingHandler() {
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxService.class)) {
                var scxService = clazz.getAnnotation(ScxService.class);
                Arrays.stream(clazz.getMethods()).filter(method -> method.isAnnotationPresent(ScxMapping.class) && !Arrays.asList(scxService.excludeMethod()).contains(method.getName())).forEach(method -> {
                    method.setAccessible(true);
                    var scxMapping = method.getAnnotation(ScxMapping.class);
                    var url = scxMapping.useMethodNameAsUrl() ? StringUtils.cleanHttpUrl("api", StringUtils.toLowerCaseFirstOne(clazz.getSimpleName()), method.getName())
                            : StringUtils.cleanHttpUrl(scxService.value(), scxMapping.value());
                    if (router.get(url) != null) {
                        StringUtils.println(url + " 重复 !!!", StringUtils.Color.RED);
                    } else {
                        router.put(url, new ScxRouteHandler(method, ScxContext.getBean(clazz), scxMapping));
                    }
                });
            }
        });
    }


    public static ScxRouteHandler getRouteHandler(String uri, HttpMethod method) {
        return router.get(uri);
    }

    public static Map<String, ScxRouteHandler> getRouter() {
        return router;
    }

    public static void init() {

    }
}
