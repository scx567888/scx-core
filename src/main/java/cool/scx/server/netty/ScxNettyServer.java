package cool.scx.server.netty;

import cool.scx.boot.ScxConfig;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;

public class ScxNettyServer {

    public static void init() {
        var s = new ScxNettyServer();
        s.start();
    }

    public void start() {
        Map<String, ScxRouteHandler> router = ScxRouter.getRouter();
        router.forEach((a, b) -> {
            System.out.println(Arrays.toString(b.scxMapping.httpMethod()) + "  " + a);
        });
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        bootstrap.group(boss, work)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                                  @Override
                                  protected void initChannel(SocketChannel sc) throws Exception {
                                      ChannelPipeline pipeline = sc.pipeline();
                                      pipeline.addLast(new HttpServerCodec());// http 编解码
                                      pipeline.addLast(new HttpObjectAggregator(512 * 1024)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
                                      pipeline.addLast(new ScxHttpRequestHandler());// 请求处理器
                                  }
                              }
                );

        ChannelFuture f;
        try {
            f = bootstrap.bind(new InetSocketAddress(ScxConfig.port)).sync();
            StringUtils.println("服务器启动成功... http://" + NetUtils.getLocalAddress() + ":" + ScxConfig.port + "/", StringUtils.Color.GREEN);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
