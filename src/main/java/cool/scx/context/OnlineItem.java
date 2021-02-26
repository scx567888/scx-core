package cool.scx.context;

import io.vertx.core.http.ServerWebSocket;

/**
 * <p>OnlineItem class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class OnlineItem {
    public ServerWebSocket webSocket;
    public String username;

    /**
     * <p>Constructor for OnlineItem.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     * @param username  a {@link java.lang.String} object.
     */
    public OnlineItem(ServerWebSocket webSocket, String username) {
        this.webSocket = webSocket;
        this.username = username;
    }
}
