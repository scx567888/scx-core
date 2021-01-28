package cool.scx.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.annotation.ScxMapping;
import cool.scx.util.ObjectUtils;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class ScxRouteHandler {
    public final Method method;
    public final Object example;
    public final ScxMapping scxMapping;

    public ScxRouteHandler(Method method, Object example, ScxMapping scxMapping) {
        this.method = method;
        this.example = example;
        this.scxMapping = scxMapping;
    }

    public static Object[] getHandlerParamsFromJson(RoutingContext ctx, Parameter[] parameters, String[] parameterNames) {
        var handlerParams = new Object[parameters.length];
        String jsonStr = ctx.request().method() != HttpMethod.GET ? ctx.getBodyAsString() : "";
        JsonNode rootJsonNode = null;
        try {
            rootJsonNode = ObjectUtils.OBJECT_MAPPER.readTree(jsonStr);
        } catch (JsonProcessingException ignored) {

        }
        for (int i = 0; i < handlerParams.length; i++) {
            var nowType = parameters[i].getType();
            var nowName = parameterNames[i];
            //先尝试将 body 中的数据进行转换
            if (parameters.length == 1) {
                try {
                    handlerParams[i] = ObjectUtils.OBJECT_MAPPER.readValue(jsonStr, nowType);
                } catch (Exception e) {
                    try {
                        handlerParams[i] = ObjectUtils.OBJECT_MAPPER.treeToValue(rootJsonNode.get(nowName), nowType);
                    } catch (Exception ignored) {

                    }
                }
            } else {
                try {
                    handlerParams[i] = ObjectUtils.OBJECT_MAPPER.treeToValue(rootJsonNode.get(nowName), nowType);
                } catch (Exception ignored) {

                }
            }
        }
        return handlerParams;
    }

    public static Object[] getHandlerParamsFromFormAttributes(RoutingContext ctx, Parameter[] parameters, String[] parameterNames) {
        var handlerParams = new Object[parameters.length];
        var request = ctx.request();
        for (int i = 0; i < handlerParams.length; i++) {
            handlerParams[i] = ObjectUtils.parseSimpleType(request.getFormAttribute(parameterNames[i]), parameters[i].getType());
        }
        return handlerParams;
    }

    public static Object[] getHandlerParamsFromPath(RoutingContext ctx, Parameter[] parameters, String[] parameterNames) {
        var pathParams = ctx.pathParams();
        var handlerParams = new Object[parameters.length];
        for (int i = 0; i < handlerParams.length; i++) {
            handlerParams[i] = ObjectUtils.parseSimpleType(pathParams.get(parameterNames[i]), parameters[i].getType());
        }
        return handlerParams;
    }

    public static Object[] getHandlerParamsFromQuery(RoutingContext ctx, Parameter[] parameters, String[] parameterNames) {
        var queryParams = ctx.queryParams();
        var handlerParams = new Object[parameters.length];
        for (int i = 0; i < handlerParams.length; i++) {
            handlerParams[i] = ObjectUtils.parseSimpleType(queryParams.get(parameterNames[i]), parameters[i].getType());
        }
        return handlerParams;
    }

    public Object getResult(RoutingContext ctx) {
        var parameters = method.getParameters();
        var parameterNames = ObjectUtils.u.getParameterNames(method);
        var handlerParamsFromJson = getHandlerParamsFromJson(ctx, parameters, parameterNames);
        var handlerParamsFromFormAttributes = getHandlerParamsFromFormAttributes(ctx, parameters, parameterNames);
        var handlerParamsFromPath = getHandlerParamsFromPath(ctx, parameters, parameterNames);
        var handlerParamsFromQuery = getHandlerParamsFromQuery(ctx, parameters, parameterNames);

        var finalHandlerParams = new Object[parameters.length];
        for (int i = 0; i < finalHandlerParams.length; i++) {
            var nowType = parameters[i].getType();
            if (nowType == RoutingContext.class) {
                finalHandlerParams[i] = ctx;
                continue;
            }
            if (handlerParamsFromJson[i] != null) {
                finalHandlerParams[i] = handlerParamsFromJson[i];
                continue;
            }
            if (handlerParamsFromFormAttributes[i] != null) {
                finalHandlerParams[i] = handlerParamsFromFormAttributes[i];
                continue;
            }
            if (handlerParamsFromPath[i] != null) {
                finalHandlerParams[i] = handlerParamsFromPath[i];
                continue;
            }
            if (handlerParamsFromQuery[i] != null) {
                finalHandlerParams[i] = handlerParamsFromQuery[i];
            }
        }

        try {
            return method.invoke(example, finalHandlerParams);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.getCause();
            return "服务器发生错误";
        }

    }

}
