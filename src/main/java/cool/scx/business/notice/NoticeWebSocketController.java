package cool.scx.business.notice;

import cool.scx.annotation.websocket.ScxWebSocketController;
import cool.scx.base.websocket.BaseWebSocketController;
import cool.scx.boot.ScxContext;
import cool.scx.business.user.User;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知公告
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxWebSocketController("/notice")
public class NoticeWebSocketController implements BaseWebSocketController {

    //这里存储所有处于连接状态的
    public final static List<WebSocketSession> WEB_SOCKET_SESSIONS = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(ServerWebSocket webSocket) {
        var binaryHandlerID = webSocket.binaryHandlerID();
        //这里我们 不对权限进行处理
        var webSocketSession = new WebSocketSession(binaryHandlerID, webSocket, null);
        var sessionItem = WEB_SOCKET_SESSIONS.stream().filter(u ->
                u.handlerID.equals(binaryHandlerID)).findAny().orElse(null);

        if (sessionItem == null) {
            WEB_SOCKET_SESSIONS.add(webSocketSession);
        }
        StringUtils.printlnAutoColor(binaryHandlerID + " 连接了!!!");
        StringUtils.printlnAutoColor("当前总连接数 : " + WEB_SOCKET_SESSIONS.size());
    }

    /**
     * 客户端终止连接
     */
    @Override
    public void onClose(ServerWebSocket webSocket) {
        //如果客户端终止连接 将此条连接作废
        WEB_SOCKET_SESSIONS.removeIf(f -> f.handlerID.equals(webSocket.binaryHandlerID()));
        System.out.println(webSocket + "关闭了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(String[] args) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket) {
        var binaryHandlerID = webSocket.binaryHandlerID();
        var map = ObjectUtils.jsonToMap(textData);
        var type = map.get("type");
        var callBackId = map.get("callBackId").toString();
        //这条信息是 登录(websocket)验证信息
        if ("login".equals(type.toString())) {
            String token = map.get("token").toString();
            User currentUser = ScxContext.getCurrentUserByToken(token);
            //这条websocket 连接验证通过
            if (currentUser != null) {
                var sessionItem = WEB_SOCKET_SESSIONS.stream().filter(u ->
                        u.handlerID.equals(binaryHandlerID)).findAny().orElse(null);
                //理论上 sessionItem 不可能为空 但是 还是应该判断一下 这里嫌麻烦 先不写了 todo
                sessionItem.user = currentUser;
                var s = Json.ok().data("callBackId", callBackId).data("message", currentUser).toString();
                webSocket.writeTextMessage(s);
                StringUtils.printlnAutoColor(currentUser.username + " 通过 websocket 连接到服务器 " + binaryHandlerID);
            }
            StringUtils.printlnAutoColor("当前总在线用户数量 : " + WEB_SOCKET_SESSIONS.stream().filter(u ->
                    u.user != null).count());
        } else if ("sendMessage".equals(type.toString())) {
            //发送的用户
            var fromUser = WEB_SOCKET_SESSIONS.stream().filter(u ->
                    u.handlerID.equals(binaryHandlerID)).findAny().orElse(null);
            var username = map.get("username").toString();
            var message = map.get("message").toString();
            var toUser = WEB_SOCKET_SESSIONS.stream().filter(u ->
                    username.equals(u.user.username)).findAny().orElse(null);
            toUser.webSocket.writeTextMessage(Json.ok().data("callBackId", callBackId).data("fromUser", fromUser.user).data("message", message).toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket) {
//        System.out.println(binaryData);
    }

    public static class WebSocketSession {
        public String handlerID;
        public ServerWebSocket webSocket;
        public User user;

        public WebSocketSession(String handlerID, ServerWebSocket webSocket, User user) {
            this.handlerID = handlerID;
            this.webSocket = webSocket;
            this.user = user;
        }
    }

}
