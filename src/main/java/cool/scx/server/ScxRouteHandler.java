package cool.scx.server;

import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.annotation.ScxMapping;
import cool.scx.util.ObjectUtils;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class ScxRouteHandler {
    public final Method method;
    public final Object example;
    public final ScxMapping scxMapping;

    public ScxRouteHandler(Method method, Object example, ScxMapping scxMapping) {
        this.method = method;
        this.example = example;
        this.scxMapping = scxMapping;
    }

    private static Object getValueFormPathAndQuery(String parameterName, Class<?> parameterType, Map<String, String> pathParams, MultiMap queryParams) {
        //body 转换失败 尝试采用 pathParams
        var tempParams = pathParams.get(parameterName);
        //从 路径参数获取失败
        if (tempParams == null) {
            //尝试从 查询参数
            tempParams = queryParams.get(parameterName);
        }
        return ObjectUtils.parseSimpleType(tempParams, parameterType);
    }

    public Object getResult(RoutingContext ctx) {
        var parameters = method.getParameters();
        var parameterNames = ObjectUtils.u.getParameterNames(method);
        var handlerParams = new Object[parameters.length];
        String jsonStr = "";
        //一共有三种参数来源 优先级 body > path > query
        if (ctx.request().method() != HttpMethod.GET) {
            jsonStr = ctx.getBodyAsString();
        }
        JsonNode rootJsonNode = null;
        try {
            rootJsonNode = ObjectUtils.OBJECT_MAPPER.readTree(jsonStr);
        } catch (Exception ignored) {

        }
        var pathParams = ctx.pathParams();
        var queryParams = ctx.queryParams();
        for (int i = 0; i < parameters.length; i++) {
            var nowType = parameters[i].getType();
            var nowName = parameterNames[i];
            if (nowType == RoutingContext.class) {
                handlerParams[i] = ctx;
                continue;
            }
            try {
                //先尝试将 body 中的数据进行转换
                if (parameters.length == 1) {
                    handlerParams[i] = ObjectUtils.OBJECT_MAPPER.readValue(jsonStr, nowType);
                } else {
                    handlerParams[i] = ObjectUtils.OBJECT_MAPPER.treeToValue(rootJsonNode.get(nowName), nowType);
                    if ("".equals(handlerParams[i]) || handlerParams[i] == null) {
                        handlerParams[i] = getValueFormPathAndQuery(nowName, nowType, pathParams, queryParams);
                    }
                }
            } catch (Exception e) {
                handlerParams[i] = getValueFormPathAndQuery(nowName, nowType, pathParams, queryParams);
            }
        }

        try {
            return method.invoke(example, handlerParams);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.getCause();
            return "服务器发生错误";
        }

    }
}
