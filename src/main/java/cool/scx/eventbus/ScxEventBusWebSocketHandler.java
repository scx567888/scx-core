package cool.scx.eventbus;

import cool.scx.annotation.ScxWebSocketRoute;
import cool.scx.base.BaseWSHandler;
import cool.scx.bo.WSBody;
import cool.scx.context.ScxContext;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * 时间总线 websocket 连接处理类
 * <p>
 * 负责维护前台和后台的事件总线通讯
 *
 * @author 司昌旭
 * @version 1.0.16
 */
@ScxWebSocketRoute("/scx")
public class ScxEventBusWebSocketHandler implements BaseWSHandler {

    /**
     * 心跳检测字符
     */
    private static final String LOVE = "❤";

    /**
     * 根据 前台发送的字符串封装实体
     *
     * @param text      text
     * @param webSocket w
     * @return w
     */
    private static ScxWSBody createScxWebSocketEvent(String text, ServerWebSocket webSocket) {
        var map = ObjectUtils.jsonToMap(text);
        Object eventName = map.get("eventName");
        if (eventName != null) {
            return new ScxWSBody(new WSBody(eventName.toString(), map.get("data")), webSocket);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * onOpen
     */
    @Override
    public void onOpen(ServerWebSocket webSocket) {
        ScxContext.addOnlineItem(webSocket, null);
        Ansi.OUT.brightBlue(webSocket.binaryHandlerID() + " 连接了!!! 当前总连接数 : " + ScxContext.getOnlineItemList().size()).ln();
    }

    /**
     * {@inheritDoc}
     * <p>
     * onClose
     */
    @Override
    public void onClose(ServerWebSocket webSocket) {
        //如果客户端终止连接 将此条连接作废
        ScxContext.removeOnlineItemByWebSocket(webSocket);
        Ansi.OUT.brightRed(webSocket.binaryHandlerID() + " 关闭了!!! 当前总连接数 : " + ScxContext.getOnlineItemList().size()).ln();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket) {
        //这里是心跳检测
        if (LOVE.equals(textData)) {
            webSocket.writeTextMessage(LOVE);
        } else { //这里是其他事件
            var event = createScxWebSocketEvent(textData, webSocket);
            if (event != null) {
                ScxEventBus.requestScxWebSocketEvent(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket) {
        Ansi.OUT.print("onBinaryMessage").ln();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable event, ServerWebSocket webSocket) {
        event.printStackTrace();
    }

}
