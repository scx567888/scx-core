package cool.scx.auth;

import io.vertx.core.http.ServerWebSocket;

/**
 * websocket 在线对象
 *
 * @author 司昌旭
 * @version 0.9.0
 */
public class OnlineItem {
    /**
     * 连接
     */
    public ServerWebSocket webSocket;
    /**
     * 此连接对应的用户名
     * 当初始连接的时候 username 会为空
     * 当登录成功时会通过websocket将认证成功的用户发送到服务的
     * 这时才会对 username 进行赋值
     */
    public String username;

    /**
     * OnlineItem 初始化函数
     * username 可以为空
     *
     * @param webSocket webSocket
     * @param username  username
     */
    public OnlineItem(ServerWebSocket webSocket, String username) {
        this.webSocket = webSocket;
        this.username = username;
    }
}
