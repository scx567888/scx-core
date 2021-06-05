package cool.scx.web;

import cool.scx.Scx;
import cool.scx.config.ScxConfig;
import cool.scx.exception.handler.ScxServerExceptionHandler;
import cool.scx.util.Ansi;
import cool.scx.util.NetUtils;
import cool.scx.util.Timer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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
        initServerStartSuccessHandler();
        loadServer();
        Ansi.OUT.brightYellow("服务器初始化完毕...").ln();
    }

    /**
     * <p>initServerStartSuccessHandler.</p>
     */
    public static void initServerStartSuccessHandler() {
        Scx.vertx().eventBus().consumer("startVertxServer", (message) -> {
            var port = message.body().toString();
            Ansi.OUT.green("服务器启动成功... 用时 " + Timer.stopToMillis("ScxApp") + " ms").ln();
            var httpOrHttps = ScxConfig.isOpenHttps() ? "https" : "http";
            Ansi.OUT.green("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + port + "/").ln();
            Ansi.OUT.green("> 本地 : " + httpOrHttps + "://localhost:" + port + "/").ln();
        });
    }

    private static void loadServer() {
        //创建服务器端配置文件
        var httpServerOptions = new HttpServerOptions();
        if (ScxConfig.isOpenHttps()) {
            httpServerOptions.setSsl(true)
                    .setKeyStoreOptions(new JksOptions()
                            .setPath(ScxConfig.sslPath().getPath())
                            .setPassword(ScxConfig.sslPassword()));
        }
        server = Scx.vertx().createHttpServer(httpServerOptions);
        server.requestHandler(ScxRouter::handle).webSocketHandler(ScxRouter::webSocketHandler);
        Ansi.OUT.brightYellow("已加载 " + ScxRouter.routeSize() + " 个 Http 路由 !!!").ln();
        Ansi.OUT.brightYellow("已加载 " + ScxRouter.webSocketRouteSize() + " 个 WebSocket 路由 !!!").ln();
    }

    /**
     * 启动服务器
     */
    public static void startServer() {
        server.listen(port, http -> {
            if (http.succeeded()) {
                Scx.vertx().eventBus().publish("startVertxServer", port);
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

    /**
     * 停止服务器
     */
    public static void stopServer() {
        stopServer(c -> {
            if (c.succeeded()) {
                Ansi.OUT.brightRed("服务器已停止...").ln();
            } else {
                Ansi.OUT.brightRed("服务器停止失败...").ln();
            }
        });
    }

    /**
     * 停止服务器
     *
     * @param resultHandler 回调参数
     */
    public static void stopServer(Handler<AsyncResult<Void>> resultHandler) {
        server.close(resultHandler);
    }

}
