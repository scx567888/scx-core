package cool.scx.server.handler;

import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

/**
 * <p>DefaultLoginAndPermsHandler class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class DefaultLoginAndPermsHandler implements LoginAndPermsHandler {
    /** {@inheritDoc} */
    @Override
    public void noLogin(RoutingContext context) {
        Json.fail(Json.ILLEGAL_TOKEN, "未登录").sendToClient(context);
    }

    /** {@inheritDoc} */
    @Override
    public void noPerms(RoutingContext context) {
        Json.fail(Json.NO_PERMISSION, "没有权限").sendToClient(context);
    }
}
