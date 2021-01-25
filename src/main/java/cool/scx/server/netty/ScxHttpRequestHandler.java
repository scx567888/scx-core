package cool.scx.server.netty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.vo.Html;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class ScxHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static void fillContentType(FullHttpResponse response, ScxRouteHandler scxRouteHandler) {
        var contentType = "text/plain";
        switch (scxRouteHandler.scxMapping.returnType()) {
            case JSON:
                contentType = "application/json; charset=utf-8";
                break;
            case HTML:
                contentType = "text/html; charset=utf-8";
                break;
            default:
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    private static String getStringFormObject(Object result) {
        var aClass = result.getClass();
        if (aClass == String.class) {
            return result.toString();
        }
        if (aClass == Html.class) {
            return ((Html) result).getHtmlStr();
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        ScxRouteHandler routeHandler = ScxRouter.getRouteHandler(req.uri(), req.method());
        Object result = routeHandler.getResult(req);

        var msg = getStringFormObject(result);
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        fillContentType(response, routeHandler);

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
