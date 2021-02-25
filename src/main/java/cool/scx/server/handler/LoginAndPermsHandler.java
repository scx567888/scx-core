package cool.scx.server.handler;

import io.vertx.ext.web.RoutingContext;

/**
 * <p>LoginAndPermsHandler interface.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public interface LoginAndPermsHandler {

    /**
     * <p>noLogin.</p>
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object.
     */
    void noLogin(RoutingContext routingContext);

    /**
     * <p>noPerms.</p>
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object.
     */
    void noPerms(RoutingContext routingContext);

}
