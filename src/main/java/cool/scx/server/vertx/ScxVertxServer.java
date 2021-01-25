package cool.scx.server.vertx;

import cool.scx.boot.ScxConfig;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;

public final class ScxVertxServer extends AbstractVerticle {

    public static EventBus eventBus;
    public static HttpServer server;

    public static void init() {
        Vertx.vertx().deployVerticle(new ScxVertxServer());
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static void stopServer() {
        server.close();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        server = vertx.createHttpServer();
        eventBus = vertx.eventBus();
        var router = ScxRouterFactory.getRouter(vertx);
        if (ScxConfig.showLog) {
            router.getRoutes().forEach(route -> System.out.println(route.methods() + "   " + route.getPath()));
        }
        server.requestHandler(router).listen(ScxConfig.port, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                StringUtils.println("服务器启动成功... http://" + NetUtils.getLocalAddress() + ":" + ScxConfig.port + "/", StringUtils.Color.GREEN);
            } else {
                http.cause().printStackTrace();
                startPromise.fail(http.cause());
            }
        });

    }

}
