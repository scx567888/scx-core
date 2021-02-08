package cool.scx.server;

import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.annotation.ScxMapping;
import cool.scx.util.ObjectUtils;
import cool.scx.vo.Json;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
     * <p>getHandlerParamsFromJson.</p>
     *
     * @param ctx        a {@link io.vertx.ext.web.RoutingContext} object.
     * @param parameters an array of {@link java.lang.reflect.Parameter} objects.
     * @return an array of {@link java.lang.Object} objects.
     */
    public static Object[] getHandlerParamsFromJson(RoutingContext ctx, Parameter[] parameters) {
        var handlerParams = new Object[parameters.length];
        String jsonStr = ctx.request().method() != HttpMethod.GET ? ctx.getBodyAsString() : "";
        JsonNode rootJsonNode = ObjectUtils.JsonToTree(jsonStr);
        for (int i = 0; i < handlerParams.length; i++) {
            var nowType = parameters[i].getType();
            var nowName = parameters[i].getName();
            //先尝试将 body 中的数据进行转换
            handlerParams[i] = ObjectUtils.jsonToBean(jsonStr, nowType);
            if (handlerParams[i] == null && rootJsonNode != null) {
                handlerParams[i] = ObjectUtils.jsonNodeToBean(rootJsonNode.get(nowName), nowType);
            }
        }
        return handlerParams;
    }

    /**
     * <p>getHandlerParamsFromFormAttributes.</p>
     *
     * @param ctx        a {@link io.vertx.ext.web.RoutingContext} object.
     * @param parameters an array of {@link java.lang.reflect.Parameter} objects.
     * @return an array of {@link java.lang.Object} objects.
     */
    public static Object[] getHandlerParamsFromFormAttributes(RoutingContext ctx, Parameter[] parameters) {
        var handlerParams = new Object[parameters.length];
        var request = ctx.request();
        for (int i = 0; i < handlerParams.length; i++) {
            handlerParams[i] = ObjectUtils.parseSimpleType(request.getFormAttribute(parameters[i].getName()), parameters[i].getType());
        }
        return handlerParams;
    }

    /**
     * <p>getHandlerParamsFromPath.</p>
     *
     * @param ctx        a {@link io.vertx.ext.web.RoutingContext} object.
     * @param parameters an array of {@link java.lang.reflect.Parameter} objects.
     * @return an array of {@link java.lang.Object} objects.
     */
    public static Object[] getHandlerParamsFromPath(RoutingContext ctx, Parameter[] parameters) {
        var pathParams = ctx.pathParams();
        var handlerParams = new Object[parameters.length];
        for (int i = 0; i < handlerParams.length; i++) {
            handlerParams[i] = ObjectUtils.parseSimpleType(pathParams.get(parameters[i].getName()), parameters[i].getType());
        }
        return handlerParams;
    }

    /**
     * <p>getHandlerParamsFromQuery.</p>
     *
     * @param ctx        a {@link io.vertx.ext.web.RoutingContext} object.
     * @param parameters an array of {@link java.lang.reflect.Parameter} objects.
     * @return an array of {@link java.lang.Object} objects.
     */
    public static Object[] getHandlerParamsFromQuery(RoutingContext ctx, Parameter[] parameters) {
        var queryParams = ctx.queryParams();
        var handlerParams = new Object[parameters.length];
        for (int i = 0; i < handlerParams.length; i++) {
            handlerParams[i] = ObjectUtils.parseSimpleType(queryParams.get(parameters[i].getName()), parameters[i].getType());
        }
        return handlerParams;
    }

    /**
     * <p>getResult.</p>
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object getResult(RoutingContext ctx) {
        var parameters = method.getParameters();
        var handlerParamsFromJson = getHandlerParamsFromJson(ctx, parameters);
        var handlerParamsFromFormAttributes = getHandlerParamsFromFormAttributes(ctx, parameters);
        var handlerParamsFromPath = getHandlerParamsFromPath(ctx, parameters);
        var handlerParamsFromQuery = getHandlerParamsFromQuery(ctx, parameters);

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
            e.printStackTrace();
            return Json.fail(Json.SYSTEM_ERROR, e.getMessage());
        }
    }

}
