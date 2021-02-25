package cool.scx.server.handler;

import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

public class DefaultLoginAndPermsHandler implements LoginAndPermsHandler {
    @Override
    public void noLogin(RoutingContext context) {
        Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
    }

    @Override
    public void noPerms(RoutingContext context) {
        Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
    }
}
