package cool.scx._test;

import cool.scx.bo.WSBody;
import cool.scx.context.ScxContext;

/**
 * 发送消息测试 handler
 *
 * @author 司昌旭
 * @version 1.1.17
 */
public class SendMessageHandler {

    /**
     * 发送消息
     *
     * @param wsBody a {@link io.vertx.core.json.JsonObject} object
     */
    public static void sendMessage(WSBody wsBody) {
        //先获取消息
        var message = wsBody.data().asText().split("");
        System.out.println(wsBody.toJson());
        wsBody.webSocket().writeTextMessage("123123123123");
        System.out.println(wsBody.webSocket());
        //循环发送
        for (var aChar : message) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //向所有在线用户发送
            var onlineItemList = ScxContext.getOnlineItemList();
            for (var onlineItem : onlineItemList) {
                onlineItem.send("writeMessage", aChar);
            }
        }
    }
}
