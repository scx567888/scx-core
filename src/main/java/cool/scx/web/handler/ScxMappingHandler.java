package cool.scx.web.handler;

import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.annotation.FromBody;
import cool.scx.annotation.FromPath;
import cool.scx.annotation.FromQuery;
import cool.scx.annotation.ScxMapping;
import cool.scx.auth.AuthUser;
import cool.scx.auth.ScxAuth;
import cool.scx.bo.FileUpload;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.exception.HttpRequestException;
import cool.scx.exception.UnsupportedMediaTypeException;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.BaseVo;
import cool.scx.vo.Json;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
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
class ScxMappingHandler implements Handler<RoutingContext> {

    public final Method method;
    public final ScxMapping methodScxMapping;
    public final ScxMapping classScxMapping;
    public final Object example;
    public final int order;
    public final Class<?> clazz;
    public final String url;
    public final Set<HttpMethod> httpMethods;
    public final String permStr;


    /**
     * <p>Constructor for ScxRouteHandler.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     * @param clazz  a {@link java.lang.Class} object.
     */
    public ScxMappingHandler(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.classScxMapping = clazz.getAnnotation(ScxMapping.class);
        this.method = method;
        this.methodScxMapping = method.getAnnotation(ScxMapping.class);
        this.example = ScxContext.getBean(clazz);
        this.url = getUrl();
        this.order = getOrder();
        this.httpMethods = getHttpMethod();
        this.permStr = clazz.getSimpleName() + ":" + method.getName();
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

    private static Object getParamFromMap(Map<String, ?> map, String value, boolean merge, Parameter parameter, boolean required) throws UnsupportedMediaTypeException {
        if (StringUtils.isEmpty(value)) {
            value = parameter.getName();
        }
        try {
            if (merge) {
                return ObjectUtils.mapToBean(map, parameter.getType());
            } else {
                return ObjectUtils.parseSimpleType(map.get(value), parameter.getType());
            }
        } catch (Exception e) {
            if (required) {
                throw new UnsupportedMediaTypeException();
            } else {
                return null;
            }
        }
    }

    private static Object getParamFromBody(JsonNode jsonNode, Map<String, Object> formAttributesMap, String bodyParamValue, Parameter parameter, boolean required) throws UnsupportedMediaTypeException {
        if (formAttributesMap.size() == 0) {
            var j = jsonNode;
            if (bodyParamValue != null) {
                if ("".equals(bodyParamValue)) {
                    bodyParamValue = parameter.getName();
                }
                var split = bodyParamValue.split("\\.");
                for (String s : split) {
                    if (j != null) {
                        j = j.get(s);
                    }
                }
            }
            try {
                return ObjectUtils.jsonNodeToBean(j, parameter.getParameterizedType());
            } catch (Exception e) {
                if (required) {
                    throw new UnsupportedMediaTypeException();
                } else {
                    return null;
                }
            }
        } else {
            try {
                if (StringUtils.isEmpty(bodyParamValue)) {
                    return ObjectUtils.mapToBean(formAttributesMap, parameter.getType());
                } else {
                    return ObjectUtils.parseSimpleType(formAttributesMap.get(bodyParamValue), parameter.getType());
                }
            } catch (Exception e) {
                if (required) {
                    throw new UnsupportedMediaTypeException();
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * <p>multiMapToMap.</p>
     *
     * @param multiMap a {@link io.vertx.core.MultiMap} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, Object> multiMapToMap(MultiMap multiMap) {
        var map = new HashMap<String, Object>();
        for (var m : multiMap) {
            map.put(m.getKey(), m.getValue());
        }
        return map;
    }


    /**
     * 同时验证登录和权限
     *
     * @param context 上下文对象
     * @return 验证结果 true 为 允许继续向下进行处理 false 表示截至继续运行
     */
    private boolean checkedLoginAndPerms(RoutingContext context) {
        //如果 不检查登录 对应的也没有必要检查 权限 所以直接返回 true
        if (!methodScxMapping.checkedLogin()) {
            return true;
        } else {
            //当前登录的用户
            AuthUser currentUser = ScxAuth.getLoginUser();
            //session 中没有用户证明没有登录 返回 false
            if (currentUser == null) {
                ScxAuth.authHandler().noLoginHandler(ScxAuth.getDevice(context), context);
                return false;
            } else {
                //这里就是 需要登录 并且 能够获取到当前登录用户的
                //不需要 检查权限 直接返回 true
                if (!methodScxMapping.checkedPerms()) {
                    return true;
                } else {
                    //这里就是 管理员级别  不受权限验证
                    if (currentUser._IsAdmin()) {
                        return true;
                    } else {
                        //获取用户全部的权限字符串
                        var permStrByUser = ScxAuth.authHandler().getPerms(currentUser);
                        if (permStrByUser.contains(permStr)) {
                            return true;
                        } else {
                            ScxAuth.authHandler().noPermsHandler(ScxAuth.getDevice(context), context);
                            return false;
                        }
                    }
                }
            }
        }
    }

    private String getUrl() {
        return methodScxMapping.useMethodNameAsUrl() && "".equals(methodScxMapping.value()) ?
                StringUtils.clearHttpUrl("api", getApiNameByControllerName(clazz), method.getName())
                : StringUtils.clearHttpUrl(classScxMapping.value(), methodScxMapping.value());
    }

    private int getOrder() {
        var o = 0;
        char[] chars = url.toCharArray();
        for (char aChar : chars) {
            if (aChar == ':') {
                o = o + 1;
            } else if (aChar == '*') {
                o = o + 2;
            }
        }
        return o;
    }

    /**
     * <p>getResult.</p>
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a {@link java.lang.Object} object.
     * @throws java.lang.Exception if any.
     */
    private Object getResult(RoutingContext ctx) throws Exception {
        Set<FileUpload> uploadFiles = ctx.get("uploadFiles");
        if (uploadFiles == null) {
            uploadFiles = new HashSet<>();
        }
        var parameters = method.getParameters();
        //先从多个来源获取参数 并缓存起来
        var jsonNode = ObjectUtils.JsonToTree(ctx.request().method() != HttpMethod.GET ? ctx.getBodyAsString() : "");
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
            if (nowType == Device.class) {
                finalHandlerParams[i] = ScxAuth.getDevice(ctx);
                continue;
            }
            if (nowType == FileUpload.class) {
                String name = parameters[i].getName();
                finalHandlerParams[i] = uploadFiles.stream().filter(c -> name.equals(c.name)).findAny().orElse(null);
                continue;
            }
            var bodyParam = parameters[i].getAnnotation(FromBody.class);
            if (bodyParam != null) {
                finalHandlerParams[i] = getParamFromBody(jsonNode, formAttributes, bodyParam.value(), parameters[i], bodyParam.required());
                continue;
            }
            var queryParam = parameters[i].getAnnotation(FromQuery.class);
            if (queryParam != null) {
                finalHandlerParams[i] = getParamFromMap(queryParams, queryParam.value(), queryParam.merge(), parameters[i], queryParam.required());
                continue;
            }
            var pathParam = parameters[i].getAnnotation(FromPath.class);
            if (pathParam != null) {
                finalHandlerParams[i] = getParamFromMap(pathParams, pathParam.value(), pathParam.merge(), parameters[i], pathParam.required());
                continue;
            }
            //------ 这里针对没有注解的参数进行赋值猜测 ---------------
            //  从 body 里进行猜测 先尝试 根据参数名称进行转换
            finalHandlerParams[i] = getParamFromBody(jsonNode, formAttributes, parameters[i].getName(), parameters[i], false);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            // 再尝试将整体转换为 参数
            finalHandlerParams[i] = getParamFromBody(jsonNode, formAttributes, null, parameters[i], false);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            //从查询参数里进行猜测
            finalHandlerParams[i] = getParamFromMap(queryParams, parameters[i].getName(), false, parameters[i], false);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            //从路径进行猜测
            finalHandlerParams[i] = getParamFromMap(pathParams, parameters[i].getName(), false, parameters[i], false);
        }
        return method.invoke(example, finalHandlerParams);
    }

    private Set<HttpMethod> getHttpMethod() {
        return Stream.of(methodScxMapping.method())
                .map(r -> HttpMethod.valueOf(r.toString()))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     * <p>
     * handle
     */
    @Override
    public void handle(RoutingContext context) {
        ScxContext.routingContext(context);
        //检查是否登录 并且权限是否正确
        boolean b = checkedLoginAndPerms(context);
        //这里验证失败不需要返回 因为 对相应的客户端的相应的处理已经在 checkedLoginAndPerms 中完成
        if (!b) {
            return;
        }

        var response = context.response();
        Object result;
        try {
            result = getResult(context);
        } catch (Exception e) {
            //如果是反射调用方法就使用 方法的内部异常 否则使用异常
            Throwable exception = (e instanceof InvocationTargetException) ? e.getCause() : e;
            //在此处进行对异常进行截获处理
            if (exception instanceof HttpRequestException) {
                ((HttpRequestException) exception).exceptionHandler(context);
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
                //此处暂时未对异常进行处理
                e.printStackTrace();
            }
            return;
        }
        response.end(ObjectUtils.beanToJsonUseAnnotations(result));
    }

}
