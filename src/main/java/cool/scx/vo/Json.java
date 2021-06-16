package cool.scx.vo;


import cool.scx.util.ObjectUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Json 格式的返回值
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class Json implements BaseVo {

    /**
     * 内部结构
     */
    private final JsonBody jsonBody = new JsonBody();

    /**
     * 无参构造
     */
    private Json() {
    }

    /**
     * 全参构造
     *
     * @param message 消息
     */
    private Json(String message) {
        jsonBody.message = message;
    }

    /**
     * <p>empty.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    public static Json empty() {
        return new Json();
    }

    /**
     * <p>ok.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    public static Json ok() {
        return new Json("ok");
    }

    /**
     * 普通错误
     *
     * @return json
     */
    public static Json fail() {
        return new Json("fail");
    }

    /**
     * 返回操作成功的 Json 带有消息
     *
     * @param message 自定义的成功信息
     * @return json
     */
    public static Json message(String message) {
        return new Json(message);
    }

    /**
     * 设置操作返回的数据，数据使用自定义的key存储
     *
     * @param dataKey 自定义的key
     * @param dataVal 值
     * @return json
     */
    public Json put(String dataKey, Object dataVal) {
        jsonBody.data.put(dataKey, dataVal);
        return this;
    }


    /**
     * 设置操作返回的数据集合
     *
     * @param dataVal 数据
     * @return json
     */
    public Json items(Object dataVal) {
        jsonBody.data.put("items", dataVal);
        return this;
    }

    /**
     * 返回表格方法
     *
     * @param items list 集合的数据
     * @param total 分页的总条数
     * @return json
     */
    public Json tables(Object items, Integer total) {
        jsonBody.data.put("items", items);
        jsonBody.data.put("total", total);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * sendToClient
     */
    @Override
    public void sendToClient(RoutingContext context) {
        var response = context.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        response.end(Buffer.buffer(ObjectUtils.beanToByteArrayUseAnnotations(jsonBody)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * 返回 json
     */
    @Override
    public String toString() {
        return ObjectUtils.beanToJsonUseAnnotations(jsonBody);
    }

    private static class JsonBody {
        public final Map<String, Object> data = new HashMap<>();
        public String message;
    }

}
