package cool.scx.bo;

import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.util.ObjectUtils;
import io.vertx.core.http.ServerWebSocket;

import java.io.Serializable;

/**
 * 封装的 websocket 对象
 *
 * @author scx567888
 * @version 1.1.17
 */
public class WSBody implements Serializable {

    /**
     * 消息实体
     */
    private final Body body;

    /**
     * 前台对应的 websocket 连接
     */
    private final ServerWebSocket webSocket;

    /**
     * <p>Constructor for ScxWSBody.</p>
     *
     * @param eventName a {@link cool.scx.bo.WSBody} object
     * @param data      a {@link cool.scx.bo.WSBody} object
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public WSBody(String eventName, JsonNode data, ServerWebSocket webSocket) {
        this.body = new Body(eventName, data);
        this.webSocket = webSocket;
    }

    /**
     * <p>Constructor for ScxWSBody.</p>
     *
     * @param eventName a {@link cool.scx.bo.WSBody} object
     * @param data      a {@link cool.scx.bo.WSBody} object
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public WSBody(String eventName, Object data, ServerWebSocket webSocket) {
        this.body = new Body(eventName, ObjectUtils.valueToTree(data));
        this.webSocket = webSocket;
    }

    /**
     * <p>data.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.JsonNode} object
     */
    public JsonNode data() {
        return body.data;
    }

    /**
     * <p>eventName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String eventName() {
        return body.eventName;
    }

    /**
     * <p>toJson.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String toJson() {
        return ObjectUtils.beanToJsonUseAnnotations(this.body);
    }

    /**
     * <p>webSocket.</p>
     *
     * @return a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public ServerWebSocket webSocket() {
        return webSocket;
    }

    /**
     * 前台和后台发送 websocket 消息的 封装体
     *
     * @author scx567888
     * @version 1.2.2
     */
    private static class Body {

        /**
         * 事件名称 事件总线用
         */
        public final String eventName;

        /**
         * 消息体
         */
        public final JsonNode data;

        /**
         * <p>Constructor for WSBody.</p>
         *
         * @param eventName a {@link java.lang.String} object
         * @param data      a {@link java.lang.Object} object
         */
        public Body(String eventName, JsonNode data) {
            this.eventName = eventName;
            this.data = data;
        }

    }
}
