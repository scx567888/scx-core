package cool.scx.web;

import cool.scx.boot.ScxTimer;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.exception.handler.ScxServerExceptionHandler;
import cool.scx.util.Ansi;
import cool.scx.util.NetUtils;
import cool.scx.web.handler.ScxRequestHandler;
import cool.scx.web.handler.ScxWebSocketHandler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;

import java.net.BindException;

/**
 * scx 服务器
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class ScxServer {

    /**
     * 端口号
     */
    public static int port = ScxConfig.port();
    /**
     * 后台服务器
     */
    private static HttpServer server;

    /**
     * 初始化 服务器
     */
    public static void initServer() {
        Ansi.OUT.brightYellow("正在初始化服务器...").ln();
        loadServer();
        Ansi.OUT.brightYellow("服务器初始化完毕...").ln();
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
                            .setPath(ScxConfig.sslPath().getPath())
                            .setPassword(ScxConfig.sslPassword()));
        }
        server = ScxContext.VERTX.createHttpServer(httpServerOptions);
        server.requestHandler(requestHandler).webSocketHandler(webSocketHandler);
    }

    /**
     * 启动服务器
     */
    public static void startServer() {
        server.listen(port, http -> {
            if (http.succeeded()) {
                Ansi.OUT.green("服务器启动成功... 用时 " + ScxTimer.timerStop() + " ms").ln();
                var httpOrHttps = ScxConfig.openHttps() ? "https" : "http";
                Ansi.OUT.green("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + port + "/").ln();
                Ansi.OUT.green("> 本地 : " + httpOrHttps + "://localhost:" + port + "/").ln();
            } else {
                var cause = http.cause();
                if (cause instanceof BindException) {
                    ScxServerExceptionHandler.bindExceptionHandler();
                } else {
                    cause.printStackTrace();
                }
            }
        });
    }

}
