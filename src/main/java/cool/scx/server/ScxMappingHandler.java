package cool.scx.server;

import cool.scx.annotation.*;
import cool.scx.base.BaseVo;
import cool.scx.boot.ScxContext;
import cool.scx.business.user.User;
import cool.scx.exception.HttpResponseException;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>ScxRouteHandler class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class ScxMappingHandler implements Handler<RoutingContext> {
    public final Method method;
    public final ScxMapping scxMapping;
    public final ScxController scxController;
    public final Object example;
    public final boolean isRegexUrl;
    public final Class<?> clazz;
    public final String url;
    public final Set<HttpMethod> httpMethods;

    /**
     * <p>Constructor for ScxRouteHandler.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     * @param clazz  a {@link java.lang.Class} object.
     */
    public ScxMappingHandler(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.scxController = clazz.getAnnotation(ScxController.class);
        this.method = method;
        this.scxMapping = method.getAnnotation(ScxMapping.class);
        this.example = ScxContext.getBean(clazz);
        this.url = getUrl();
        this.isRegexUrl = isRegexUrl();
        this.httpMethods = getHttpMethod();
    }

    /**
     * 清理分隔符错误的路径如 清理前 : a/b//c -- 清理后 : /a/b/c
     *
     * @param url 需要清理的 url 集合
     * @return 清理后的结果
     */
    private static String clearHttpUrl(String... url) {
        return Arrays.stream(String.join("/", url).split("/")).filter(s -> !"".equals(s)).collect(Collectors.joining("/", "/", ""));
    }

    /**
     * 根据 controller 获取 api 的 名称
     * 例 1 : UserController -- user
     * 例 2 : AppleColorController -- appleColor
     *
     * @param controllerClass controller 的 Class
     * @return 处理后的路径
     */
    public static String getApiNameByControllerName(Class<?> controllerClass) {
        var s = controllerClass.getSimpleName().replace("Controller", "");
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private static Object getParamFromPath(Map<String, String> pathParams, String pathParamValue, boolean pathParamPolymerize, Parameter parameter) {
        if (StringUtils.isEmpty(pathParamValue)) {
            pathParamValue = parameter.getName();
        }
        if (pathParamPolymerize) {
            return ObjectUtils.mapToBean(pathParams, parameter.getType());
        } else {
            return ObjectUtils.parseSimpleType(pathParams.get(pathParamValue), parameter.getType());
        }
    }

    private static Object getParamFromQuery(Map<String, Object> queryParams, String queryParamValue, boolean queryParamPolymerize, Parameter parameter) {
        if (StringUtils.isEmpty(queryParamValue)) {
            queryParamValue = parameter.getName();
        }
        if (queryParamPolymerize) {
            return ObjectUtils.mapToBean(queryParams, parameter.getType());
        } else {
            return ObjectUtils.parseSimpleType(queryParams.get(queryParamValue), parameter.getType());
        }
    }

    private static Object getParamFromBody(String jsonStr, Map<String, Object> formAttributesMap, String bodyParamValue, Parameter parameter) {
        if (formAttributesMap.size() == 0) {
            if (StringUtils.isEmpty(bodyParamValue)) {
                return ObjectUtils.jsonToBean(jsonStr, parameter.getType());
            } else {
                var jsonNode = ObjectUtils.JsonToTree(jsonStr);
                var split = bodyParamValue.split("\\.");
                for (String s : split) {
                    if (jsonNode != null) {
                        jsonNode = jsonNode.get(s);
                    }
                }
                return ObjectUtils.jsonNodeToBean(jsonNode, parameter.getParameterizedType());
            }
        } else {
            if (StringUtils.isEmpty(bodyParamValue)) {
                return ObjectUtils.mapToBean(formAttributesMap, parameter.getType());
            } else {
                return ObjectUtils.parseSimpleType(formAttributesMap.get(bodyParamValue), parameter.getType());
            }
        }
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

    public static Map<String, Object> multiMapToMap(MultiMap multiMap) {
        var map = new HashMap<String, Object>();
        for (var m : multiMap) {
            map.put(m.getKey(), m.getValue());
        }
        return map;
    }

    private boolean checkedLogin(RoutingContext ctx) {
        User currentUser = ScxContext.getCurrentUser(ctx);
        if (currentUser == null) {
            Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(ctx);
            return false;
        }
        return true;
    }

    private String getUrl() {
        return scxMapping.useMethodNameAsUrl() && "".equals(scxMapping.value()) ?
                clearHttpUrl("api", getApiNameByControllerName(clazz), method.getName())
                : clearHttpUrl(scxController.value(), scxMapping.value());
    }

    private boolean isRegexUrl() {
        return url.contains(":") || url.contains("*");
    }

    /**
     * <p>getResult.</p>
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a {@link java.lang.Object} object.
     * @throws java.lang.Exception if any.
     */
    private Object getResult(RoutingContext ctx) throws Exception {
        var parameters = method.getParameters();
        //先从多个来源获取参数 并缓存起来
        var jsonStr = ctx.request().method() != HttpMethod.GET ? ctx.getBodyAsString() : "";
        var formAttributes = multiMapToMap(ctx.request().formAttributes());
        var queryParams = multiMapToMap(ctx.queryParams());
        var pathParams = ctx.pathParams();

        var finalHandlerParams = new Object[parameters.length];
        for (int i = 0; i < finalHandlerParams.length; i++) {
            var nowType = parameters[i].getType();
            if (nowType == RoutingContext.class) {
                finalHandlerParams[i] = ctx;
                continue;
            }
            var bodyParam = parameters[i].getAnnotation(BodyParam.class);
            if (bodyParam != null) {
                finalHandlerParams[i] = getParamFromBody(jsonStr, formAttributes, bodyParam.value(), parameters[i]);
                continue;
            }
            var queryParam = parameters[i].getAnnotation(QueryParam.class);
            if (queryParam != null) {
                finalHandlerParams[i] = getParamFromQuery(queryParams, queryParam.value(), queryParam.polymerize(), parameters[i]);
                continue;
            }
            var pathParam = parameters[i].getAnnotation(PathParam.class);
            if (pathParam != null) {
                finalHandlerParams[i] = getParamFromPath(pathParams, pathParam.value(), pathParam.polymerize(), parameters[i]);
                continue;
            }
            //------这里针对没有注解的参数进行赋值猜测---------------
            //从 body 里进行猜测 先尝试将整体转换为 参数
            finalHandlerParams[i] = getParamFromBody(jsonStr, formAttributes, "", parameters[i]);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            //再尝试 根据 参数名称进行转换
            finalHandlerParams[i] = getParamFromBody(jsonStr, formAttributes, parameters[i].getName(), parameters[i]);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            //从查询参数里进行猜测
            finalHandlerParams[i] = getParamFromQuery(queryParams, parameters[i].getName(), false, parameters[i]);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            //从路径进行猜测
            finalHandlerParams[i] = getParamFromPath(pathParams, parameters[i].getName(), false, parameters[i]);
            //---------------------
        }
        return method.invoke(example, finalHandlerParams);
    }

    private Set<HttpMethod> getHttpMethod() {
        return Stream.of(scxMapping.method())
                .map(r -> HttpMethod.valueOf(r.toString()))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(RoutingContext context) {
        if (!scxMapping.unCheckedLogin()) {
            boolean b = checkedLogin(context);
            if (!b) {
                return;
            }
        }

        var response = context.response();
        Object result;
        try {
            result = getResult(context);
        } catch (Exception e) {
            var cause = e.getCause();
            // 我们后面会自定义一些其他 自定义异常
            //在此处进行截获处理
            if (cause instanceof HttpResponseException) {
                ((HttpResponseException) cause).errFun.accept(context);
                context.end();
                return;
            }
            Json.fail(Json.SYSTEM_ERROR, e.getMessage()).sendToClient(context);
            e.printStackTrace();
            return;
        }
        if (result instanceof String || result instanceof Integer || result instanceof Double || result instanceof Boolean) {
            response.putHeader("Content-Type", "text/plain; charset=utf-8");
            response.end(result.toString());
            return;
        }
        if (result instanceof BaseVo) {
            try {
                ((BaseVo) result).sendToClient(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        response.end(ObjectUtils.beanToJson(result));
    }

}
