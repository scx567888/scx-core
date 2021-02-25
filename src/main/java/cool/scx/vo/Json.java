package cool.scx.vo;


import cool.scx.base.http.BaseVo;
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
     * 成功的 code
     */
    public static final int SUCCESS_CODE = 20;
    /**
     * 失败的 code
     */
    public static final int FAIL_CODE = -1;
    /**
     * 非法的令牌 code
     */
    public static final int ILLEGAL_TOKEN = 58;
    /**
     * session 过期
     */
    public static final int SESSION_TIMEOUT = 44;
    /**
     * 没有权限
     */
    public static final int NO_PERMISSION = 51;
    /**
     * 后台系统错误
     */
    public static final int SYSTEM_ERROR = 50;

    private final Map<String, Object> jsonMap = new HashMap<>();


    /**
     * 无参构造：操作成功返回的响应信息
     */
    public Json() {
        jsonMap.put("code", SUCCESS_CODE);
        jsonMap.put("message", "ok");
    }

    /**
     * 全参构造
     *
     * @param code    代码
     * @param message 消息
     */
    public Json(int code, String message) {
        jsonMap.put("code", code);
        jsonMap.put("message", message);
    }

    /**
     * <p>ok.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    public static Json ok() {
        return new Json();
    }

    /**
     * 返回操作成功的 Json 带有消息
     *
     * @param message 自定义的成功信息
     * @return json
     */
    public static Json ok(String message) {
        return new Json(SUCCESS_CODE, message);
    }

    /**
     * 普通错误
     *
     * @param message 返回错误信息
     * @return json
     */
    public static Json fail(String message) {
        return new Json(FAIL_CODE, message);
    }

    /**
     * 自定义 code 码
     *
     * @param code    自定义的 code 码
     * @param message 消息
     * @return json
     */
    public static Json fail(int code, String message) {
        return new Json(code, message);
    }


    /**
     * 设置操作返回的数据，数据使用自定义的key存储
     *
     * @param dataKey 自定义的key
     * @param dataVal 值
     * @return json
     */
    public Json data(String dataKey, Object dataVal) {
        jsonMap.put(dataKey, dataVal);
        return this;
    }


    /**
     * 设置操作返回的数据集合
     *
     * @param dataVal 数据
     * @return json
     */
    public Json items(Object dataVal) {
        jsonMap.put("items", dataVal);
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
        jsonMap.put("items", items);
        jsonMap.put("total", total);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToClient(RoutingContext context) {
        var response = context.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        response.end(Buffer.buffer(ObjectUtils.beanToByteArray(jsonMap)));
    }

    @Override
    public String toString() {
        return ObjectUtils.beanToJson(jsonMap);
    }
}
