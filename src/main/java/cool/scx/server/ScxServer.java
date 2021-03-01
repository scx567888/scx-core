package cool.scx.server;

import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Color;
import cool.scx.server.http.ScxRequestHandler;
import cool.scx.server.websocket.ScxWebSocketHandler;
import cool.scx.util.LogUtils;
import cool.scx.util.NetUtils;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;

/**
 * scx 服务器
 *
 * @author 司昌旭
 * @version 0.5.8
 */
public final class ScxServer {

    /**
     * 后台服务器
     */
    private final static HttpServer server;

    /**
     * 服务器是否在运行中
     */
    private static boolean serverRunning = false;

    static {
        LogUtils.println("正在部署服务...", Color.BRIGHT_BLUE);
        server = ScxContext.VERTX.createHttpServer(getHttpServerOptions());
        initVertServer();
        startVertxServer();
    }


    /**
     * <p>init.</p>
     */
    public static void init() {
        LogUtils.println("服务部署完毕...", Color.BRIGHT_BLUE);
    }

    /**
     * <p>startVertxServer.</p>
     */
    public static void startVertxServer() {
        if (serverRunning) {
            return;
        }
        var port = checkPort(ScxConfig.port());
        server.listen(http -> {
            if (http.succeeded()) {
                LogUtils.println("服务器启动成功...", Color.GREEN);
                var httpOrHttps = ScxConfig.openHttps() ? "https" : "http";
                LogUtils.println("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + port + "/", Color.GREEN);
                LogUtils.println("> 本地 : " + httpOrHttps + "://localhost:" + port + "/", Color.GREEN);
                ScxContext.eventBus().publish("startVertxServer", "");
                serverRunning = true;
            } else {
                http.cause().printStackTrace();
            }
        });
    }

    /**
     * <p>stopServer.</p>
     */
    public static void stopVertxServer() {
        server.close(c -> {
            if (c.succeeded()) {
                ScxContext.eventBus().publish("stopVertxServer", "");
                serverRunning = false;
            }
        });
        LogUtils.println("服务器已停止...", Color.BRIGHT_RED);
    }

    /**
     * 初始化 服务器
     */
    private static void initVertServer() {
        var requestHandler = new ScxRequestHandler();
        var webSocketHandler = new ScxWebSocketHandler();
        server.requestHandler(requestHandler).webSocketHandler(webSocketHandler);
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
        httpServerOptions.setPort(ScxConfig.port());
        if (ScxConfig.openHttps()) {
            httpServerOptions
                    .setSsl(true)
                    .setKeyStoreOptions(new JksOptions()
                            .setPath(ScxConfig.certPath().getPath())
                            .setPassword(ScxConfig.certPassword()));
        }
        LogUtils.println("服务器配置文件初始化完毕...", Color.YELLOW);
        return httpServerOptions;
    }


    /**
     * <p>isServerRunning.</p>
     *
     * @return a boolean.
     */
    public static boolean isServerRunning() {
        return serverRunning;
    }

    /**
     * 检查端口号是否可以使用
     * 当端口号不可以使用时会 将端口号进行累加 1 直到端口号可以使用
     *
     * @param p 需要检查的端口号
     * @return 可以使用的端口号
     */
    private static int checkPort(int p) {
        while (NetUtils.isLocalePortUsing(p)) {
            p = p + 1;
            LogUtils.println("✘ 端口号 [ " + (p - 1) + " ] 已被占用 !!!         \t -->\t 新端口号 : " + p, Color.RED);
        }
        return p;
    }
}
