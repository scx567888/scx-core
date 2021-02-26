package cool.scx.context;

import io.vertx.core.http.ServerWebSocket;

public class OnlineItem {
    public ServerWebSocket webSocket;
    public String username;

    public OnlineItem(ServerWebSocket webSocket, String username) {
        this.webSocket = webSocket;
        this.username = username;
    }
}
