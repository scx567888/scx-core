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

    public ScxWSBody(WSBody wsBody, ServerWebSocket webSocket) {
        this.wsBody = wsBody;
        this.webSocket = webSocket;
    }

}
