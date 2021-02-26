package cool.scx.server;

import cool.scx.config.ScxConfig;
import cool.scx.enumeration.Color;
import cool.scx.server.http.ScxRequestHandler;
import cool.scx.server.websocket.ScxWebSocketHandler;
import cool.scx.util.LogUtils;
import cool.scx.util.NetUtils;
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
    private static EventBus eventBus;
    /**
     * Constant <code>server</code>
     */
    private static HttpServer server;
    /**
     * 服务器是否在运行中
     */
    private static boolean serverState = false;

    static {
        LogUtils.println("正在部署服务...", Color.BRIGHT_BLUE);
        Vertx.vertx().deployVerticle(new ScxServer());
    }

    /**
     * <p>Getter for the field <code>serverState</code>.</p>
     *
     * @return a boolean.
     */
    public static boolean getServerState() {
        return serverState;
    }

    /**
     * <p>init.</p>
     */
    public static void init() {
        LogUtils.println("服务部署完毕...", Color.BRIGHT_BLUE);
    }

    /**
     * 获取 eventbus
     *
     * @return a {@link io.vertx.core.eventbus.EventBus} object.
     */
    public static EventBus getEventBus() {
        return eventBus;
    }

    /**
     * <p>startVertxServer.</p>
     */
    public static void startVertxServer() {
        if (serverState) {
            return;
        }
        server.listen(http -> {
            if (http.succeeded()) {
                LogUtils.println("服务器启动成功...", Color.GREEN);
                var httpOrHttps = ScxConfig.openHttps ? "https" : "http";
                LogUtils.println("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + ScxConfig.port + "/", Color.GREEN);
                LogUtils.println("> 本地 : " + httpOrHttps + "://localhost:" + ScxConfig.port + "/", Color.GREEN);
            } else {
                http.cause().printStackTrace();
            }
        });
        serverState = true;
    }

    /**
     * <p>stopServer.</p>
     */
    public static void stopVertxServer() {
        server.close();
        LogUtils.println("服务器已停止...", Color.BRIGHT_RED);
        serverState = false;
    }

    /**
     * 初始化 服务器
     *
     * @param vertx vertx 实例
     */
    private static void initVertServer(Vertx vertx) {
        server = vertx.createHttpServer(getHttpServerOptions());
        server.requestHandler(new ScxRequestHandler(vertx)).webSocketHandler(new ScxWebSocketHandler());
        LogUtils.println("服务器初始化完毕...", Color.GREEN);
    }

    /**
     * 创建 服务端配置项
     *
     * @return 服务器配置项
     */
    private static HttpServerOptions getHttpServerOptions() {
        LogUtils.println("服务器配置文件初始化中...", Color.YELLOW);
        var httpServerOptions = new HttpServerOptions();
        httpServerOptions.setPort(ScxConfig.port);
        if (ScxConfig.openHttps) {
            httpServerOptions
                    .setSsl(true)
                    .setKeyStoreOptions(new JksOptions()
                            .setPath(ScxConfig.certificatePath.getPath())
                            .setPassword(ScxConfig.certificatePassword));
        }
        LogUtils.println("服务器配置文件初始化完毕...", Color.YELLOW);
        return httpServerOptions;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public void start(Promise<Void> startPromise) {
        initVertServer(vertx);
        startVertxServer();
        eventBus = vertx.eventBus();
    }

}
