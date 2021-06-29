package cool.scx.handler;

import cool.scx.eventbus.ScxEventBus;
import cool.scx.util.Ansi;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * <p>DefaultHandler class.</p>
 *
 * @author 司昌旭
 * @version 1.2.2
 */
public class DefaultHandler {

    /**
     * <p>initDefaultHandler.</p>
     */
    public static void initDefaultHandler() {
        ScxEventBus.consumer("login", (Message<JsonObject> m) -> {
            Ansi.OUT.print("login").ln();
            cool.scx.eventbus.handler.LoginByWebSocketHandler.loginByWebSocket(m.body());
        });
        ScxEventBus.consumer("hello", (Message<JsonObject> m) -> {
            String s = cool.scx.eventbus.handler.SendMessageByWebsocketHandler.sendMessage(m.body());
            m.reply(s);
        });
    }
}
