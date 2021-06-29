package cool.scx.eventbus;

import cool.scx.bo.WSBody;
import io.vertx.core.http.ServerWebSocket;

/**
 * 封装的 websocket 对象
 *
 * @author 司昌旭
 * @version 1.1.17
 */
class ScxWSBody {

    /**
     * 消息实体
     */
    public final WSBody wsBody;

    /**
     * 前台对应的 websocket 连接
     */
    public final ServerWebSocket webSocket;

    /**
     * <p>Constructor for ScxWSBody.</p>
     *
     * @param wsBody    a {@link cool.scx.bo.WSBody} object
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public ScxWSBody(WSBody wsBody, ServerWebSocket webSocket) {
        this.wsBody = wsBody;
        this.webSocket = webSocket;
    }

}
