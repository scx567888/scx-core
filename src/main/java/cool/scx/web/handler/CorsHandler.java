package cool.scx.web.handler;

import cool.scx.auth.ScxAuth;
import cool.scx.config.ScxConfig;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.CorsHandlerImpl;

import java.util.Set;

/**
 * 注册 跨域 处理器
 *
 * @author scx567888
 * @version 1.1.9
 */
public class CorsHandler implements Handler<RoutingContext> {

    private final CorsHandlerImpl vertxCorsHandler;

    /**
     * <p>Constructor for CorsHandler.</p>
     */
    public CorsHandler() {
        var allowedHeaders = Set.of("x-requested-with", "Access-Control-Allow-Origin",
                "origin", "Content-Type", "accept", "X-PINGARUNER", ScxAuth.TOKEN_KEY, ScxAuth.DEVICE_KEY);

        var allowedMethods = Set.of(HttpMethod.GET, HttpMethod.POST,
                HttpMethod.OPTIONS, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT);
        this.vertxCorsHandler = (CorsHandlerImpl) new CorsHandlerImpl(ScxConfig.allowedOrigin()).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).allowCredentials(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(RoutingContext ctx) {
        vertxCorsHandler.handle(ctx);
    }
}
