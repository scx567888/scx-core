package cool.scx.vo;


import java.util.HashMap;

public final class Json extends HashMap<String, Object> {

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


    /**
     * 无参构造：操作成功返回的响应信息
     */
    public Json() {
        this.put("code", SUCCESS_CODE);
        this.put("message", "ok");
    }

    /**
     * 全参构造
     *
     * @param code    代码
     * @param message 消息
     */
    public Json(int code, String message) {
        this.put("code", code);
        this.put("message", message);
    }

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
     * @param message 带有自定义 code 码的错误信息
     * @return json
     */
    public static Json fail(int code, String message) {
        return new Json(code, message);
    }


    /**
     * 设置操作返回的数据，数据使用自定义的key存储
     */
    public Json data(String dataKey, Object dataVal) {
        this.put(dataKey, dataVal);
        return this;
    }

    /**
     * 设置操作返回的数据集合
     */
    public Json items(Object dataVal) {
        this.put("items", dataVal);
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
        this.put("items", items);
        this.put("total", total);
        return this;
    }
}
