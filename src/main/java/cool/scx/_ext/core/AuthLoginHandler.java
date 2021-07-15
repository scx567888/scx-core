package cool.scx._ext.core;

import cool.scx.annotation.ScxEventConsumer;
import cool.scx.annotation.ScxService;
import cool.scx.auth.AuthUser;
import cool.scx.auth.ScxAuth;
import cool.scx.bo.WSBody;
import cool.scx.context.ScxContext;
import cool.scx.util.Ansi;

/**
 * 通过 websocket 认证 token
 *
 * @author scx567888
 * @version 1.1.17
 */
@ScxService
public class AuthLoginHandler {

    /**
     * <p>loginByWebSocket.</p>
     *
     * @param wsBody {@link io.vertx.core.json.JsonObject} object
     */
    @ScxEventConsumer("auth-token")
    public void loginByWebSocket(WSBody wsBody) {
        String token = wsBody.data().get("token").asText();
        if (token != null) {
            Ansi.OUT.green(token).ln();
            AuthUser loginUserByToken = ScxAuth.getLoginUserByToken(token);
            //这条websocket 连接验证通过
            if (loginUserByToken != null) {
                ScxContext.addOnlineItem(wsBody.webSocket(), loginUserByToken._UniqueID());
                Ansi.OUT.brightGreen(wsBody.webSocket().binaryHandlerID() + " 登录了!!! 登录的 ID 为 : " + loginUserByToken._UniqueID()).ln();
                Ansi.OUT.brightYellow("当前总在线用户数量 : " + ScxContext.getOnlineUserCount()).ln();
            }
        }
    }

}
