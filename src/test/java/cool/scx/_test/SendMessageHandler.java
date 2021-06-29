package cool.scx._test;

import cool.scx.context.ScxContext;
import io.vertx.core.json.JsonObject;

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
     * @param json a {@link io.vertx.core.json.JsonObject} object
     */
    public static void sendMessage(JsonObject json) {
        //先获取消息
        var message = json.getString("message").split("");

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
