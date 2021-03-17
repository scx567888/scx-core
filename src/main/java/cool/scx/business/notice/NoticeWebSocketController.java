package cool.scx.business.notice;

import cool.scx.annotation.ScxWebSocketController;
import cool.scx.base.BaseWebSocketController;
import cool.scx.context.OnlineItem;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;
import cool.scx.vo.Json;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

/**
 * 通知公告
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxWebSocketController("/notice")
public class NoticeWebSocketController implements BaseWebSocketController {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(ServerWebSocket webSocket) {
        ScxContext.addOnlineItem(webSocket, null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 客户端终止连接
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
        var binaryHandlerID = webSocket.binaryHandlerID();
        var map = ObjectUtils.jsonToMap(textData);
        var type = map.get("type");
        var callBackId = map.get("callBackId").toString();
        //这条信息是 登录(websocket)验证信息
        if ("login".equals(type.toString())) {
            String token = map.get("token").toString();
            Device device = Device.ADMIN;//todo 此处获取不正确
            var nowLoginUser = ScxContext.getLoginUserByToken(device, token);
            //这条websocket 连接验证通过
            if (nowLoginUser != null) {
                ScxContext.addOnlineItem(webSocket, nowLoginUser.username);
                //理论上 sessionItem 不可能为空 但是 还是应该判断一下 这里嫌麻烦 先不写了 todo
                var s = Json.ok().data("callBackId", callBackId).data("message", nowLoginUser).toString();
                webSocket.writeTextMessage(s);
                Ansi.OUT.brightGreen(binaryHandlerID + " 登录了!!! 登录的用户名 : " + nowLoginUser.username).ln();
            }
            Ansi.OUT.brightYellow("当前总在线用户数量 : " + ScxContext.getOnlineUserCount()).ln();
        } else if ("sendMessage".equals(type.toString())) {
            //发送的用户
            var username = map.get("username").toString();
            var message = map.get("message").toString();
            var fromUser = ScxContext.getOnlineItemByWebSocket(webSocket);

//            var toUser = ScxContext.getOnlineItemByUserName(username);

            //测试群发
            var toUser = ScxContext.getOnlineItemList();
            for (OnlineItem onlineItem : toUser) {
                if (!onlineItem.username.equals(fromUser.username)) {
                    onlineItem.webSocket.writeTextMessage(Json.ok().data("callBackId", callBackId).data("fromUser", fromUser.username).data("message", message).toString());
                }
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket) {
//        System.out.println(binaryData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable event, ServerWebSocket webSocket) {
        event.printStackTrace();
    }

}
