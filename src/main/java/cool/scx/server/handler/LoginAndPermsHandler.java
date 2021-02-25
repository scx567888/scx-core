package cool.scx.server.handler;

import io.vertx.ext.web.RoutingContext;

public interface LoginAndPermsHandler {

    void noLogin(RoutingContext routingContext);

    void noPerms(RoutingContext routingContext);

}
