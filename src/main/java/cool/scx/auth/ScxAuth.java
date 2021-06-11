package cool.scx.auth;

import cool.scx.auth.exception.UnknownDeviceException;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.DeviceType;
import cool.scx.exception.AuthException;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.Ansi;
import cool.scx.util.StringUtils;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 提供基本的认证逻辑
 * todo 这里面的大部分方法逻辑有问题 需要修改
 *
 * @author 司昌旭
 * @version 1.1.4
 */
public final class ScxAuth {

    /**
     * 获取 token 的标识字段
     */
    public static final String TOKEN_KEY = "S-Token";

    /**
     * 获取 设备 的标识字段
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
     * AuthHandler 实例 主要用来 获取登录用户的 权限等信息
     */
    private static AuthHandler AUTH_HANDLER;

    /**
     * 获取 AuthHandler
     *
     * @return a {@link cool.scx.auth.AuthHandler} object
     */
    public static AuthHandler authHandler() {
        return AUTH_HANDLER;
    }

    /**
     * 移除认证用户
     *
     * @return a boolean.
     */
    public static boolean removeAuthUser(RoutingContext ctx) {
        String token = getTokenByDevice(ctx);
        return LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
    }

    /**
     * 移除认证用户
     * <p>
     * 使用默认的 路由上下文
     *
     * @return a boolean.
     */
    public static boolean removeAuthUser() {
        var ctx = ScxContext.routingContext();
        String token = getTokenByDevice(ctx);
        return LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
    }

    /**
     * 添加用户到 登录列表中
     *
     * @param ctx      a {@link io.vertx.ext.web.RoutingContext} object
     * @param authUser a {@link cool.scx.auth.AuthUser} object
     * @return a {@link java.lang.String} object
     * @throws cool.scx.exception.AuthException if any.
     */
    public static String addAuthUser(RoutingContext ctx, AuthUser authUser) throws AuthException {
        String token;
        var loginDevice = getDevice(ctx);
        var uniqueID = authUser._UniqueID();
        //先判断登录用户的来源
        if (loginDevice == DeviceType.ADMIN || loginDevice == DeviceType.APPLE || loginDevice == DeviceType.ANDROID) {
            token = StringUtils.getUUID();
        } else if (loginDevice == DeviceType.WEBSITE) {
            token = getTokenByCookie(ctx);
        } else {
            throw new UnknownDeviceException();
        }
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.uniqueID.equals(uniqueID) && loginDevice == u.loginDevice).findAny().orElse(null);
        if (sessionItem == null) {
            LOGIN_ITEMS.add(new LoginItem(token, uniqueID, loginDevice));
        } else {
            sessionItem.token = token;
        }
        return token;
    }

    /**
     * 根据 token 获取用户
     *
     * @param token  a {@link java.lang.String} object.
     * @param device a {@link DeviceType} object.
     * @return a {@link cool.scx.auth.AuthUser} object.
     */
    public static AuthUser getLoginUserByToken(DeviceType device, String token) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.token.equals(token) && u.loginDevice == device).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        //todo  这里每次都从数据库中获取用户是为了保证权限设置的及时性 但是为了 性能 此处应该做一个缓存
        return AUTH_HANDLER.getAuthUser(sessionItem.uniqueID);
    }

    /**
     * 获取用户
     *
     * @return a {@link cool.scx.auth.AuthUser} object.
     */
    public static AuthUser getLoginUser(RoutingContext ctx) {
        return getLoginUserByToken(getDevice(ctx), getTokenByDevice(ctx));
    }

    /**
     * 获取用户
     * <p>
     * 使用默认的路由上下文
     *
     * @return a {@link cool.scx.auth.AuthUser} object.
     */
    public static AuthUser getLoginUser() {
        var ctx = ScxContext.routingContext();
        return getLoginUserByToken(getDevice(ctx), getTokenByDevice(ctx));
    }

    /**
     * 获取用户的设备
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link DeviceType} object
     */
    public static DeviceType getDevice(RoutingContext routingContext) {
        String device = routingContext.request().getHeader(DEVICE_KEY);
        if (device == null || device.equalsIgnoreCase("WEBSITE")) {
            return DeviceType.WEBSITE;
        }
        if (device.equalsIgnoreCase("APPLE")) {
            return DeviceType.APPLE;
        }
        if (device.equalsIgnoreCase("ADMIN")) {
            return DeviceType.ADMIN;
        }
        if (device.equalsIgnoreCase("ANDROID")) {
            return DeviceType.ANDROID;
        } else {
            return DeviceType.UNKNOWN;
        }
    }

    public static DeviceType getDevice() {
        var ctx = ScxContext.routingContext();
        return getDevice(ctx);
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
        ScxModuleHandler.iterateClass(c -> {
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

    /**
     * 直接添加一个 loginItem
     * <p>
     * 注意此方法可能有安全性问题
     * <p>
     * 不建议用户使用
     */
    public static void addLoginItem(LoginItem loginItem) {
        LOGIN_ITEMS.add(loginItem);
    }

}
