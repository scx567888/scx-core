package cool.scx.context;

import cool.scx.boot.ScxPlugins;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.config.ScxConfig;
import cool.scx.dao.BaseDao;
import cool.scx.dao.SQLRunner;
import cool.scx.dao.annotation.ScxModel;
import cool.scx.dao.type.FixTableResult;
import cool.scx.service.annotation.ScxService;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;
import cool.scx.web.annotation.ScxController;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>ScxContext class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxContext {
    /**
     * userService 实例 主要用来 获取登录用户的 权限等信息
     */
    public static final UserService USER_SERVICE;

    /**
     * 全局 vert.x
     */
    public static final Vertx VERTX = Vertx.vertx();
    /**
     * 存储所有在线的 连接
     */
    private static final List<OnlineItem> ONLINE_ITEMS = new ArrayList<>();
    /**
     * 存储所有 已登录 的用户信息
     * todo 这里需要 格外存储 用户登录的 来源 比如  后端  或 cms 或 安卓 或 ios
     * todo 还有 需要在 scxconfig中 添加 一个配置项 标识用户多端登录 处理方式
     * todo 比如 允许用户同时登录多个 不同的 客户端(来源一致) 或者只允许用户 在同一时间登录 (无论已经在哪里登录了)
     * todo 或者 不对登录做限制 同时允许 任意客户端(来源可以不一致) 登录任意数量的 同一用户
     */
    private static final List<LoginItem> LOGIN_ITEMS = new ArrayList<>();
    private static final Map<String, Class<?>> SCX_BEAN_CLASS_NAME_MAPPING = new HashMap<>();
    private static final AnnotationConfigApplicationContext APPLICATION_CONTEXT;
    private static final ThreadLocal<RoutingContext> ROUTING_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    static {
        Ansi.ANSI.magenta("ScxContext 初始化中...").ln();
        APPLICATION_CONTEXT = new AnnotationConfigApplicationContext(PackageUtils.getBasePackages());
        ScxPlugins.pluginsClassList.forEach(APPLICATION_CONTEXT::register);
        initScxContext();
        fixTable();
        USER_SERVICE = getBean(UserService.class);
    }

    /**
     * <p>getClassByName.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.Class} object.
     */
    public static Class<?> getClassByName(String str) {
        return SCX_BEAN_CLASS_NAME_MAPPING.get(str.toLowerCase());
    }

    private static void initScxContext() {
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxService.class) || clazz.isAnnotationPresent(ScxController.class) || clazz.isAnnotationPresent(ScxModel.class)) {
                SCX_BEAN_CLASS_NAME_MAPPING.put(clazz.getSimpleName().toLowerCase(), clazz);
            }
            return true;
        });
    }

    /**
     * <p>getBean.</p>
     *
     * @param c   a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     */
    public static <T> T getBean(Class<T> c) {
        return APPLICATION_CONTEXT.getBean(c);
    }

    /**
     * <p>fixTable.</p>
     */
    public static void fixTable() {
        var noNeedFix = new AtomicBoolean(true);
        if (SQLRunner.testConnection()) {
            Ansi.ANSI.magenta("修复数据表中...").ln();
            if (ScxConfig.fixTable()) {
                SCX_BEAN_CLASS_NAME_MAPPING.forEach((k, v) -> {
                    if (v.isAnnotationPresent(ScxModel.class)) {
                        try {
                            if (BaseDao.fixTable(v) != FixTableResult.NO_NEED_TO_FIX) {
                                noNeedFix.set(false);
                            }
                        } catch (Exception ignored) {

                        }
                    }
                });
            }
        }
        if (noNeedFix.get()) {
            Ansi.ANSI.magenta("没有表需要修复...").ln();
        }
    }

    /**
     * <p>init.</p>
     */
    public static void init() {
        Ansi.ANSI.magenta("ScxContext 初始化完成...").ln();
    }

    /**
     * <p>removeLoginUserByHeader.</p>
     *
     * @return a boolean.
     */
    public static boolean removeLoginUserByHeader() {
        return removeLoginUserByHeader(routingContext());
    }

    /**
     * <p>logoutUser.</p>
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a boolean.
     */
    public static boolean removeLoginUserByHeader(RoutingContext ctx) {
        var token = ctx.request().getHeader(ScxConfig.tokenKey());
        boolean b = LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
        Ansi.ANSI.print("当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
        return b;
    }

    /**
     * <p>logoutUserByCookie.</p>
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a boolean.
     */
    public static boolean removeLoginUserByCookie(RoutingContext ctx) {
        var token = ctx.getCookie(ScxConfig.cookieKey()).getValue();
        boolean b = LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
        Ansi.ANSI.print("当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
        return b;
    }

    /**
     * <p>addUserToSession.</p>
     *
     * @param token    a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     */
    public static void addLoginItem(String token, String username) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.username.equals(username)).findAny().orElse(null);
        if (sessionItem == null) {
            LOGIN_ITEMS.add(new LoginItem(token, username));
        } else {
            sessionItem.token = token;
        }
        Ansi.ANSI.print(username + " 登录了 , 当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
    }

    /**
     * <p>getUserFromSessionByToken.</p>
     *
     * @param token a {@link java.lang.String} object.
     * @return a {@link cool.scx.business.user.User} object.
     */
    public static User getLoginUserByToken(String token) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.token.equals(token)).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        //每次都从数据库中获取用户 保证 权限设置的及时性 但是为了 性能 此处应该做缓存 todo
        return USER_SERVICE.findByUsername(sessionItem.username);
    }

    /**
     * <p>getLoginUserByHeader.</p>
     *
     * @return a {@link cool.scx.business.user.User} object.
     */
    public static User getLoginUserByHeader() {
        String token = routingContext().request().getHeader(ScxConfig.tokenKey());
        return getLoginUserByToken(token);
    }

    /**
     * <p>getCurrentUserByCookie.</p>
     *
     * @return a {@link cool.scx.business.user.User} object.
     */
    public static User getLoginUserByCookie() {
        String token = routingContext().getCookie(ScxConfig.cookieKey()).getValue();
        return getLoginUserByToken(token);
    }

    /**
     * <p>addOnlineItem.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     * @param username  a {@link java.lang.String} object.
     */
    public static void addOnlineItem(ServerWebSocket webSocket, String username) {
        var binaryHandlerID = webSocket.binaryHandlerID();
        //看看这个相对应的连接 是不是 已经注册到 ONLINE_ITEMS 中了 如果已经存在 就不重写注册了 而是直接更新 username
        //有点像  HashMap 的逻辑
        var onlineItem = ONLINE_ITEMS.stream().filter(u ->
                u.webSocket.binaryHandlerID().equals(binaryHandlerID)).findAny().orElse(null);
        if (onlineItem == null) {
            //先生成一个 在线用户的对象
            var newOnlineItem = new OnlineItem(webSocket, username);
            ONLINE_ITEMS.add(newOnlineItem);
        } else {
            onlineItem.username = username;
        }
        Ansi.ANSI.brightBlue(binaryHandlerID + " 连接了!!! 当前总连接数 : " + ONLINE_ITEMS.size()).ln();
    }

    /**
     * <p>removeOnlineItemByWebSocket.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     * @return a boolean.
     */
    public static boolean removeOnlineItemByWebSocket(ServerWebSocket webSocket) {
        return ONLINE_ITEMS.removeIf(f -> f.webSocket.binaryHandlerID().equals(webSocket.binaryHandlerID()));
    }

    /**
     * <p>getOnlineUserCount.</p>
     *
     * @return a long.
     */
    public static long getOnlineUserCount() {
        return ONLINE_ITEMS.stream().filter(u -> u.username != null).count();
    }

    /**
     * <p>getOnlineItemByWebSocket.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     * @return a {@link cool.scx.context.OnlineItem} object.
     */
    public static OnlineItem getOnlineItemByWebSocket(ServerWebSocket webSocket) {
        var binaryHandlerID = webSocket.binaryHandlerID();
        return ONLINE_ITEMS.stream().filter(u -> u.webSocket.binaryHandlerID().equals(binaryHandlerID)).findAny().orElse(null);
    }

    /**
     * 根据用户名获取所有的在线对象
     *
     * @param username a {@link java.lang.String} object.
     * @return a {@link cool.scx.context.OnlineItem} object.
     */
    public static OnlineItem getOnlineItemByUserName(String username) {
        return ONLINE_ITEMS.stream().filter(u -> u.username.equals(username)).findAny().orElse(null);
    }

    /**
     * 获取当前所有在线的连接对象
     *
     * @return 当前所有在线的连接对象
     */
    public static List<OnlineItem> getOnlineItemList() {
        return ONLINE_ITEMS;
    }

    /**
     * 获取全局的事件总线
     *
     * @return 全局的事件总线
     */
    public static EventBus eventBus() {
        return VERTX.eventBus();
    }

    /**
     * 获取当前线程的 RoutingContext (只限在 scx mapping 注解的方法及其调用链上)
     *
     * @return 当前线程的 RoutingContext
     */
    public static RoutingContext routingContext() {
        return ROUTING_CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * 设置当前线程的 routingContext
     * 此方法正常之给 scxMappingHandler 调用
     * 若无特殊需求 不必调用此方法
     *
     * @param routingContext 要设置的 routingContext
     */
    public static void routingContext(RoutingContext routingContext) {
        ROUTING_CONTEXT_THREAD_LOCAL.set(routingContext);
    }

}
