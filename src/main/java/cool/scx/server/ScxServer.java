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
    private static HttpServer server;

    /**
     * 服务器是否在运行中
     */
    private static boolean serverRunning = false;

    /**
     * 初始化 服务器
     */
    public static void initServer() {
        LogUtils.println("正在初始化服务器...", Color.BRIGHT_YELLOW);
        loadServer();
        LogUtils.println("服务器初始化完毕...", Color.BRIGHT_YELLOW);
    }

    private static void loadServer() {
        //先获取 handler 此处每次都重新获取是因为 handler 所扫描的类 是可以根据 scxConfig 进行配置的 所以不能为 final
        ScxRequestHandler requestHandler = new ScxRequestHandler();
        ScxWebSocketHandler webSocketHandler = new ScxWebSocketHandler();
        //创建服务器端配置文件
        var httpServerOptions = new HttpServerOptions();
        if (ScxConfig.openHttps()) {
            httpServerOptions.setSsl(true)
                    .setKeyStoreOptions(new JksOptions()
                            .setPath(ScxConfig.certPath().getPath())
                            .setPassword(ScxConfig.certPassword()));
        }
        server = ScxContext.VERTX.createHttpServer(httpServerOptions);
        server.requestHandler(requestHandler).webSocketHandler(webSocketHandler);
    }

    /**
     * <p>reloadServer.</p>
     */
    public static void reloadServer() {
        LogUtils.println("正在重新加载服务器...", Color.BRIGHT_BLUE);
        loadServer();
        LogUtils.println("正在重新加载服务器完毕...", Color.GREEN);
    }

    /**
     * <p>startVertxServer.</p>
     */
    public static void startServer() {
        if (serverRunning) {
            return;
        }
        var port = checkPort(ScxConfig.port());
        server.listen(port, http -> {
            if (http.succeeded()) {
                LogUtils.println("服务器启动成功...", Color.GREEN);
                var httpOrHttps = ScxConfig.openHttps() ? "https" : "http";
                LogUtils.println("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + port + "/", Color.GREEN);
                LogUtils.println("> 本地 : " + httpOrHttps + "://localhost:" + port + "/", Color.GREEN);
                ScxContext.eventBus().publish("startVertxServer", "");
                serverRunning = true;
            } else {
                Throwable cause = http.cause();
                cause.printStackTrace();
            }
        });
    }

    /**
     * <p>stopServer.</p>
     */
    public static void stopServer() {
        server.close(c -> {
            if (c.succeeded()) {
                ScxContext.eventBus().publish("stopVertxServer", "");
                serverRunning = false;
            }
        });
        LogUtils.println("服务器已停止...", Color.BRIGHT_RED);
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
