package cool.scx.server;

import cool.scx.boot.ScxConfig;
import cool.scx.enumeration.Color;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;

/**
 * <p>ScxVertxServer class.</p>
 *
 * @author 司昌旭
 * @version 0.5.8
 */
public final class ScxServer extends AbstractVerticle {

    /**
     * Constant <code>eventBus</code>
     */
    public static EventBus eventBus;
    /**
     * Constant <code>server</code>
     */
    public static HttpServer server;

    /**
     * <p>init.</p>
     */
    public static void init() {
        Vertx.vertx().deployVerticle(new ScxServer());
    }

    /**
     * <p>Getter for the field <code>eventBus</code>.</p>
     *
     * @return a {@link io.vertx.core.eventbus.EventBus} object.
     */
    public static EventBus getEventBus() {
        return eventBus;
    }

    /**
     * <p>stopServer.</p>
     */
    public static void stopServer() {
        server.close();
    }


    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public void start(Promise<Void> startPromise) {
        var httpServerOptions = new HttpServerOptions();
        httpServerOptions.setPort(ScxConfig.port);
        if (ScxConfig.openHttps) {
            httpServerOptions
                    .setSsl(true)
                    .setKeyStoreOptions(new JksOptions()
                            .setPath(ScxConfig.certificatePath.getPath())
                            .setPassword(ScxConfig.certificatePassword));
        }
        server = vertx.createHttpServer(httpServerOptions);
        eventBus = vertx.eventBus();
        var router = ScxRouterFactory.getRouter(vertx);
        server.requestHandler(router).listen(http -> {
            if (http.succeeded()) {
                startPromise.complete();
                StringUtils.println("服务器启动成功...", Color.GREEN);
                var httpOrHttps = ScxConfig.openHttps ? "https" : "http";
                StringUtils.println("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + ScxConfig.port + "/", Color.GREEN);
                StringUtils.println("> 本地 : " + httpOrHttps + "://localhost:" + ScxConfig.port + "/", Color.GREEN);
            } else {
                http.cause().printStackTrace();
                startPromise.fail(http.cause());
            }
        });

    }

}
