package cool.scx.web;

import cool.scx.Scx;
import cool.scx.config.ScxConfig;
import cool.scx.gui.ScxServerGUIHandler;
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
 * @author scx567888
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

    static {
        Scx.vertx().eventBus().consumer("startVertxServer", (message) -> {
            var port = message.body().toString();
            Ansi.out().green("服务器启动成功... 用时 " + Timer.stopToMillis("ScxApp") + " ms").println();
            var httpOrHttps = ScxConfig.isOpenHttps() ? "https" : "http";
            Ansi.out().green("> 网络 : " + httpOrHttps + "://" + NetUtils.getLocalAddress() + ":" + port + "/").println();
            Ansi.out().green("> 本地 : " + httpOrHttps + "://localhost:" + port + "/").println();
        });
    }

    /**
     * 初始化 服务器
     */
    public static void initServer() {
        loadServer();
        Ansi.out().brightYellow("服务器初始化完毕...").println();
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
                    ScxServerGUIHandler.bindExceptionHandler();
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
                Ansi.out().brightRed("服务器已停止...").println();
            } else {
                Ansi.out().brightRed("服务器停止失败...").println();
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
