package cool.scx.web.type;

/**
 * <p>HttpMethod class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public enum RequestMethod {
    POST("POST"),
    GET("GET"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    TRANCE("TRANCE"),
    CONNECT("CONNECT"),
    PATCH("PATCH "),
    OPTION("OPTION");

    private final String http_method_str;

    RequestMethod(String httpMethodStr) {
        this.http_method_str = httpMethodStr;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public String toString() {
        return this.http_method_str;
    }
}
