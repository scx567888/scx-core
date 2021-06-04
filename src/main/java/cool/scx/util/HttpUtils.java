package cool.scx.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>HttpUtils class.</p>
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public final class HttpUtils {

    /**
     * 发送 post 请求
     *
     * @param url     url
     * @param headers 自定义的 headers
     * @param body    发送的信息
     * @return 响应
     */
    public static HttpResponse<String> post(String url, Map<String, String> headers, Map<String, Object> body) {
        var httpClient = HttpClient.newHttpClient();
        var bodyBytes = ObjectUtils.beanToJson(body).getBytes(StandardCharsets.UTF_8);
        var bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(bodyBytes);
        var requestBuilder = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json;charset=utf-8");
        headers.forEach(requestBuilder::header);
        var request = requestBuilder.POST(bodyPublisher).build();
        var bodyHandler = HttpResponse.BodyHandlers.ofString();
        try {
            return httpClient.send(request, bodyHandler);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向 url 发送请求并获取响应值
     *
     * @param url     url
     * @param headers 自定义 header
     * @return 返回响应
     */
    public static HttpResponse<String> get(String url, Map<String, String> headers) {
        var httpClient = HttpClient.newHttpClient();
        var requestBuilder = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json;charset=utf-8");
        headers.forEach(requestBuilder::header);
        var request = requestBuilder.GET().build();
        var bodyHandler = HttpResponse.BodyHandlers.ofString();
        try {
            return httpClient.send(request, bodyHandler);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
