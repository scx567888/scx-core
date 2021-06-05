package cool.scx.web.handler;

import cool.scx.auth.ScxAuth;
import cool.scx.util.StringUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

/**
 * 设置 TOKEN_KEY 用于权限验证
 */
public class CookieHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        if (ctx.getCookie(ScxAuth.TOKEN_KEY) == null) {
            Cookie cookie = new CookieImpl(ScxAuth.TOKEN_KEY, StringUtils.getUUID());
            cookie.setMaxAge(60 * 60 * 24 * 7);
            ctx.addCookie(cookie);
        }
        ctx.next();
    }
}
