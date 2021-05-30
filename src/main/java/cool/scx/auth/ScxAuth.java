package cool.scx.auth;

import cool.scx.auth.exception.UnknownDeviceException;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.exception.AuthException;
import cool.scx.module.ScxModule;
import cool.scx.util.Ansi;
import cool.scx.util.StringUtils;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>ScxAuth class.</p>
 *
 * @author 司昌旭
 * @version 1.1.4
 */
public class ScxAuth {

    /**
     * Constant <code>TOKEN_KEY="S-Token"</code>
     */
    public static final String TOKEN_KEY = "S-Token";

    /**
     * Constant <code>DEVICE_KEY="S-Device"</code>
     */
    public static final String DEVICE_KEY = "S-Device";

    /**
     * 存储所有 已登录 的用户信息
     * todo 需要在 scxConfig中 添加 一个配置项 标识用户多端登录 处理方式
     * todo 比如 允许用户同时登录多个 不同的 客户端(来源一致) 或者只允许用户 在同一时间登录 (无论已经在哪里登录了)
     * todo 或者 不对登录做限制 同时允许 任意客户端(来源可以不一致) 登录任意数量的 同一用户
     */
    private static final List<LoginItem> LOGIN_ITEMS = new ArrayList<>();

    /**
     * userService 实例 主要用来 获取登录用户的 权限等信息
     */
    private static AuthHandler AUTH_HANDLER;

    /**
     * <p>authHandler.</p>
     *
     * @return a {@link cool.scx.auth.AuthHandler} object
     */
    public static AuthHandler authHandler() {
        return AUTH_HANDLER;
    }

    /**
     * <p>logoutUser.</p>
     *
     * @return a boolean.
     */
    public static boolean removeAuthUser() {
        var ctx = ScxContext.routingContext();
        String token = getTokenByDevice(ctx);
        boolean b = LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
        Ansi.OUT.print("当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
        return b;
    }

    /**
     * <p>addUserToSession.</p>
     *
     * @param ctx      a {@link io.vertx.ext.web.RoutingContext} object
     * @param authUser a {@link cool.scx.auth.AuthUser} object
     * @return a {@link java.lang.String} object
     * @throws cool.scx.exception.AuthException if any.
     */
    public static String addAuthUser(RoutingContext ctx, AuthUser authUser) throws AuthException {
        String token;
        var loginDevice = getDevice(ctx);
        var username = authUser._username();
        //先判断登录用户的来源
        if (loginDevice == Device.ADMIN || loginDevice == Device.APPLE || loginDevice == Device.ANDROID) {
            token = StringUtils.getUUID();
        } else if (loginDevice == Device.WEBSITE) {
            token = getTokenByCookie(ctx);
        } else {
            throw new UnknownDeviceException();
        }
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.username.equals(username) && loginDevice == u.loginDevice).findAny().orElse(null);
        if (sessionItem == null) {
            LOGIN_ITEMS.add(new LoginItem(loginDevice, token, username));
        } else {
            sessionItem.token = token;
        }
        Ansi.OUT.print(username + " 登录了 , 登录设备 [" + loginDevice + "] , 当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
        return token;
    }


    /**
     * <p>getUserFromSessionByToken.</p>
     *
     * @param token  a {@link java.lang.String} object.
     * @param device a {@link cool.scx.enumeration.Device} object.
     * @return a {@link cool.scx.auth.AuthUser} object.
     */
    public static AuthUser getLoginUserByToken(Device device, String token) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.token.equals(token) && u.loginDevice == device).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        //每次都从数据库中获取用户 保证 权限设置的及时性 但是为了 性能 此处应该做缓存 todo
        return AUTH_HANDLER.getAuthUser(sessionItem.username);
    }

    /**
     * <p>getLoginUserByHeader.</p>
     *
     * @return a {@link cool.scx.auth.AuthUser} object.
     */
    public static AuthUser getLoginUser() {
        var ctx = ScxContext.routingContext();
        return getLoginUserByToken(getDevice(ctx), getTokenByDevice(ctx));
    }

    /**
     * <p>getDevice.</p>
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link cool.scx.enumeration.Device} object
     */
    public static Device getDevice(RoutingContext routingContext) {
        String device = routingContext.request().getHeader(DEVICE_KEY);
        if (device == null || device.equalsIgnoreCase("WEBSITE")) {
            return Device.WEBSITE;
        }
        if (device.equalsIgnoreCase("APPLE")) {
            return Device.APPLE;
        }
        if (device.equalsIgnoreCase("ADMIN")) {
            return Device.ADMIN;
        }
        if (device.equalsIgnoreCase("ANDROID")) {
            return Device.ANDROID;
        } else {
            return Device.UNKNOWN;
        }
    }

    /**
     * 获取所有登录的用户
     *
     * @return a {@link java.util.List} object
     */
    public static List<LoginItem> getAllLoginItem() {
        return LOGIN_ITEMS;
    }

    /**
     * 根据 设备类型自行判断 获取 token
     *
     * @return a {@link java.lang.String} object
     */
    private static String getTokenByDevice(RoutingContext ctx) {
        var device = getDevice(ctx);
        switch (device) {
            case WEBSITE:
                return getTokenByCookie(ctx);
            case ADMIN:
                return getTokenByHeader(ctx);
            case APPLE:
                return getTokenByHeader(ctx);
            case ANDROID:
                return getTokenByHeader(ctx);
            default:
                return null;
        }
    }

    /**
     * 根据 cookie 获取 token
     *
     * @return a {@link java.lang.String} object
     */
    private static String getTokenByCookie(RoutingContext routingContext) {
        return routingContext.getCookie(ScxAuth.TOKEN_KEY).getValue();
    }

    /**
     * 根据 Header 获取 token
     *
     * @return a {@link java.lang.String} object
     */
    private static String getTokenByHeader(RoutingContext routingContext) {
        return routingContext.request().getHeader(TOKEN_KEY);
    }

    /**
     * 初始化认证需要的数据
     * todo 这里还需要初始化一下用户多终端登录的情况是踢出还是共存 (用配置文件)
     */
    public static void initAuth() {
        Ansi.OUT.brightGreen("ScxAuth 初始化中...").ln();
        AUTH_HANDLER = getAuthHandlerImpl();
        Ansi.OUT.brightGreen("ScxAuth 初始化完成...").ln();
    }

    /**
     * 获取 AuthHandler 实现类
     */
    @SuppressWarnings("unchecked")
    private static AuthHandler getAuthHandlerImpl() {
        AtomicReference<Class<? extends AuthHandler>> authHandlerImplClass = new AtomicReference<>();
        ScxModule.iterateClass(c -> {
            if (!c.isInterface() && AuthHandler.class.isAssignableFrom(c)) {
                if (authHandlerImplClass.get() == null) {
                    Ansi.OUT.brightGreen("已找到 [ " + AuthHandler.class.getName() + "] 的实现类 [ " + c.getName() + " ]").ln();
                } else {
                    Ansi.OUT.brightGreen("已找到 [ " + AuthHandler.class.getName() + "] 的实现类 [ " + c.getName() + " ] , 上一个实现类 [" + authHandlerImplClass.get().getName() + "] 已被覆盖").ln();
                }
                authHandlerImplClass.set((Class<? extends AuthHandler>) c);
            }
            return true;
        });

        if (authHandlerImplClass.get() == null) {
            Ansi.OUT.brightRed("Class [ " + AuthHandler.class.getName() + " ] 必须有一个实现类 !!!").ln();
            System.exit(0);
        }

        return ScxContext.getBean(authHandlerImplClass.get());

    }
}
