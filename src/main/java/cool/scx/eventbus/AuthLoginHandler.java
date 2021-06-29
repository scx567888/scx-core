package cool.scx.eventbus;

import cool.scx.util.Ansi;
import io.vertx.core.json.JsonObject;

/**
 * 通过 websocket 认证 token
 *
 * @author 司昌旭
 * @version 1.1.17
 */
public class AuthLoginHandler {

    /**
     * <p>loginByWebSocket.</p>
     *
     * @param jsonObject {@link io.vertx.core.json.JsonObject} object
     */
    public static void loginByWebSocket(JsonObject jsonObject) {
        String token = jsonObject.getString("token");
        if (token != null) {
            Ansi.OUT.green(token).ln();
//            Device device = Device.ADMIN;//todo 此处获取不正确
//            var nowLoginUser = ScxAuth.getLoginUserByToken(device, token.toString());
//            //这条websocket 连接验证通过
//            if (nowLoginUser != null) {
//                ScxContext.addOnlineItem(webSocket, nowLoginUser._UniqueID());
//                //理论上 sessionItem 不可能为空 但是 还是应该判断一下 这里嫌麻烦 先不写了 todo
//                var s = Json.ok().data("callBackId", callBackId).data("message", nowLoginUser).toString();
//                webSocket.writeTextMessage(s);
//                Ansi.OUT.brightGreen(binaryHandlerID + " 登录了!!! 登录的 ID 为 : " + nowLoginUser._UniqueID()).ln();
//            }
//            Ansi.OUT.brightYellow("当前总在线用户数量 : " + ScxContext.getOnlineUserCount()).ln();
        }
    }

}
