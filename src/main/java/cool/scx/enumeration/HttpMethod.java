package cool.scx.enumeration;

public enum HttpMethod {
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

    HttpMethod(String httpMethodStr) {
        this.http_method_str = httpMethodStr;
    }

    @Override
    public String toString() {
        return this.http_method_str;
    }
}
