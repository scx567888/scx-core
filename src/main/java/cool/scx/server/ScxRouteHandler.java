package cool.scx.server;

import cool.scx.annotation.BodyParam;
import cool.scx.annotation.PathParam;
import cool.scx.annotation.QueryParam;
import cool.scx.annotation.ScxMapping;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>ScxRouteHandler class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxRouteHandler {
    public final Method method;
    public final Object example;
    public final ScxMapping scxMapping;

    /**
     * <p>Constructor for ScxRouteHandler.</p>
     *
     * @param method     a {@link java.lang.reflect.Method} object.
     * @param example    a {@link java.lang.Object} object.
     * @param scxMapping a {@link cool.scx.annotation.ScxMapping} object.
     */
    public ScxRouteHandler(Method method, Object example, ScxMapping scxMapping) {
        this.method = method;
        this.example = example;
        this.scxMapping = scxMapping;
    }

    /**
     * <p>getResult.</p>
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object getResult(RoutingContext ctx) throws Exception {
        var parameters = method.getParameters();
        //先从多个来源获取参数 并缓存起来
        //todo 现在没有做 参数来源缓存
        var finalHandlerParams = new Object[parameters.length];
        for (int i = 0; i < finalHandlerParams.length; i++) {
            var nowType = parameters[i].getType();
            if (nowType == RoutingContext.class) {
                finalHandlerParams[i] = ctx;
                continue;
            }
            BodyParam bodyParam = parameters[i].getAnnotation(BodyParam.class);
            if (bodyParam != null) {
                finalHandlerParams[i] = getParamFromBody(ctx, bodyParam.value(), parameters[i]);
                continue;
            }
            QueryParam queryParam = parameters[i].getAnnotation(QueryParam.class);
            if (queryParam != null) {
                finalHandlerParams[i] = getParamFromQuery(ctx, queryParam.value(), queryParam.polymerize(), parameters[i]);
                continue;
            }
            PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
            if (pathParam != null) {
                finalHandlerParams[i] = getParamFromPath(ctx, pathParam.value(), pathParam.polymerize(), parameters[i]);
                continue;
            }
            //------这里针对没有注解的参数进行赋值猜测---------------
            //从 body 里进行猜测
            finalHandlerParams[i] = getParamFromBody(ctx, "", parameters[i]);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            //从查询参数里进行猜测
            finalHandlerParams[i] = getParamFromQuery(ctx, parameters[i].getName(), false, parameters[i]);
            if (finalHandlerParams[i] != null) {
                continue;
            }
            //从路径进行猜测
            finalHandlerParams[i] = getParamFromPath(ctx, parameters[i].getName(), false, parameters[i]);
            //---------------------
        }
        return method.invoke(example, finalHandlerParams);
    }

    private Object getParamFromPath(RoutingContext ctx, String pathParamValue, boolean pathParamPolymerize, Parameter parameter) {
        if (StringUtils.isEmpty(pathParamValue)) {
            pathParamValue = parameter.getName();
        }
        Map<String, String> stringStringMap = ctx.pathParams();
        if (pathParamPolymerize) {
            return ObjectUtils.mapToBean(stringStringMap, parameter.getType());
        } else {
            return ObjectUtils.parseSimpleType(stringStringMap.get(pathParamValue), parameter.getType());
        }
    }

    private Object getParamFromQuery(RoutingContext ctx, String queryParamValue, boolean queryParamPolymerize, Parameter parameter) {
        if (StringUtils.isEmpty(queryParamValue)) {
            queryParamValue = parameter.getName();
        }
        var queryParams = ctx.queryParams();
        var queryParamsMap = new HashMap<String, Object>();
        for (Map.Entry<String, String> queryParam : queryParams) {
            queryParamsMap.put(queryParam.getKey(), queryParam.getValue());
        }
        if (queryParamPolymerize) {
            return ObjectUtils.mapToBean(queryParamsMap, parameter.getType());
        } else {
            return ObjectUtils.parseSimpleType(queryParams.get(queryParamValue), parameter.getType());
        }
    }


    private Object getParamFromBody(RoutingContext ctx, String bodyParamValue, Parameter parameter) {
        var jsonStr = ctx.request().method() != HttpMethod.GET ? ctx.getBodyAsString() : "";
        if (StringUtils.isNotEmpty(jsonStr)) {
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
            var formAttributes = ctx.request().formAttributes();
            var formAttributesMap = new HashMap<String, Object>();
            for (Map.Entry<String, String> formAttribute : formAttributes) {
                formAttributesMap.put(formAttribute.getKey(), formAttribute.getValue());
            }
            if (StringUtils.isEmpty(bodyParamValue)) {
                return ObjectUtils.mapToBean(formAttributesMap, parameter.getType());
            } else {
                return ObjectUtils.parseSimpleType(formAttributes.get(bodyParamValue), parameter.getType());
            }
        }
    }

}
