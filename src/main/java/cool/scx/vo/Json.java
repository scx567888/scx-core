package cool.scx.vo;


import cool.scx.util.ObjectUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Json 格式的返回值
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class Json implements BaseVo {

    /**
     * 内部结构
     */
    private final JsonBody jsonBody = new JsonBody();

    /**
     * 无参构造 私有防止外界使用
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
     * 操作成功
     *
     * @return json
     */
    public static Json ok() {
        return new Json("ok");
    }

    /**
     * 操作失败
     *
     * @return json
     */
    public static Json fail() {
        return new Json("fail");
    }

    /**
     * 返回操作失败的 Json 带有消息
     *
     * @param failMessage 自定义的错误信息
     * @return json
     */
    public static Json fail(String failMessage) {
        return new Json(failMessage);
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
     * {@inheritDoc}
     * <p>
     * sendToClient
     */
    @Override
    public void sendToClient(RoutingContext context) {
        var response = context.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
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

    /**
     * JsonBody
     */
    private static class JsonBody {
        public final Map<String, Object> data = new HashMap<>();
        public String message;
    }

}
