package cool.scx.eventbus.handler;

import io.vertx.core.json.JsonObject;

/**
 * <p>SendMessageByWebsocketHandler class.</p>
 *
 * @author 司昌旭
 * @version 1.1.17
 */
public class SendMessageByWebsocketHandler {

    /**
     * <p>sendMessage.</p>
     *
     * @param json a {@link io.vertx.core.json.JsonObject} object
     * @return a {@link java.lang.String} object
     */
    public static String sendMessage(JsonObject json) {

        return json.toString() + "Test";
//        System.out.println(args);
//        //发送的用户
//        var username = map.get("username").toString();
//        var message = map.get("message").toString();
//        var fromUser = ScxContext.getOnlineItemByWebSocket(webSocket);
//
////            var toUser = ScxContext.getOnlineItemByUserName(username);
//
//        //测试群发
//        var toUser = ScxContext.getOnlineItemList();
//        for (OnlineItem onlineItem : toUser) {
//            if (!onlineItem.username.equals(fromUser.username)) {
//                onlineItem.webSocket.writeTextMessage(Json.ok().data("callBackId", callBackId).data("fromUser", fromUser.username).data("message", message).toString());
//            }
//        }
    }
}
