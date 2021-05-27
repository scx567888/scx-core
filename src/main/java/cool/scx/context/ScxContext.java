package cool.scx.context;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxModel;
import cool.scx.annotation.ScxService;
import cool.scx.auth.OnlineItem;
import cool.scx.config.ScxConfig;
import cool.scx.enumeration.Device;
import cool.scx.enumeration.FixTableResult;
import cool.scx.exception.handler.SQLRunnerExceptionHandler;
import cool.scx.module.ScxModule;
import cool.scx.sql.SQLHelper;
import cool.scx.sql.SQLRunner;
import cool.scx.util.Ansi;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ScxContext 上下文
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxContext {


    /**
     * 存储所有在线的 连接
     */
    private static final List<OnlineItem> ONLINE_ITEMS = new ArrayList<>();


    /**
     * scx bean 名称 和 class 对应映射
     */
    private static final Map<String, Class<?>> SCX_BEAN_CLASS_NAME_MAPPING = new HashMap<>();

    /**
     * spring 的 APPLICATION_CONTEXT
     */
    private static final AnnotationConfigApplicationContext APPLICATION_CONTEXT = new AnnotationConfigApplicationContext();

    /**
     * 路由上下文 THREAD_LOCAL
     */
    private static final ThreadLocal<RoutingContext> ROUTING_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();


    /**
     * MUST_HAVE_IMPL 注解的 mapping  , key 是 父类或接口 value 是唯一实现类
     */
    private static final Map<Class<?>, Class<?>> MUST_HAVE_IMPL_MAPPING = new HashMap<>();

    /**
     * 设备 THREAD_LOCAL
     */
    private static final ThreadLocal<Device> DEVICE_THREAD_LOCAL = new ThreadLocal<>();

    static {
        Ansi.OUT.magenta("ScxContext 初始化中...").ln();
        checkMustHaveImpl();
        APPLICATION_CONTEXT.scan(ScxModule.getAllModuleBasePackages());
        ScxModule.getAllPluginModule().forEach(m -> m.classList.forEach(APPLICATION_CONTEXT::register));
        APPLICATION_CONTEXT.refresh();
        initScxContext();
        fixTable();
    }

    /**
     * <p>device.</p>
     *
     * @return a {@link cool.scx.enumeration.Device} object.
     */
    public static Device device() {
        return DEVICE_THREAD_LOCAL.get();
    }

    /**
     * <p>getImplClassOrSelf.</p>
     *
     * @param c   a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.lang.Class} object.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getImplClassOrSelf(Class<T> c) {
        var implClass = MUST_HAVE_IMPL_MAPPING.get(c);
        if (implClass == null) {
            return c;
        } else {
            return (Class<T>) implClass;
        }
    }

    /**
     * 检查 OneAndOnlyOne 是否存在实现类
     */
    private static void checkMustHaveImpl() {
//        var classList = new ArrayList<Class<?>>();
//        ScxModule.iterateClass(c -> {
//            if (c.isAnnotationPresent(MustHaveImpl.class)) {
//                classList.add(c);
//            }
//            return true;
//        });
//
//        for (Class<?> o : classList) {
//            ScxModule.iterateClass(c -> {
//                if (c != o && !c.isInterface() && o.isAssignableFrom(c)) {
//                    var lastImpl = MUST_HAVE_IMPL_MAPPING.get(o);
//                    if (lastImpl == null) {
//                        Ansi.OUT.blue("已找到 [ " + o.getName() + "] 的实现类 [ " + c.getName() + " ]").ln();
//                    } else {
//                        Ansi.OUT.blue("已找到 [ " + o.getName() + "] 的实现类 [ " + c.getName() + " ] , 上一个实现类 [" + lastImpl.getName() + "] 已被覆盖").ln();
//                    }
//                    MUST_HAVE_IMPL_MAPPING.put(o, c);
//                }
//                return true;
//            });
//            if (MUST_HAVE_IMPL_MAPPING.get(o) == null) {
//                Ansi.OUT.brightRed("Class [ " + o.getName() + " ] 必须有一个实现类 !!!").ln();
//                System.exit(0);
//            }
//        }

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
        ScxModule.iterateClass(clazz -> {
            if (clazz.isAnnotationPresent(ScxService.class) || clazz.isAnnotationPresent(ScxController.class) || clazz.isAnnotationPresent(ScxModel.class)) {
                String className = clazz.getSimpleName().toLowerCase();
                Class<?> aClass = SCX_BEAN_CLASS_NAME_MAPPING.get(className);
                if (aClass == null) {
                    SCX_BEAN_CLASS_NAME_MAPPING.put(clazz.getSimpleName().toLowerCase(), clazz);
                } else {
                    SCX_BEAN_CLASS_NAME_MAPPING.put(clazz.getName(), clazz);
                    Ansi.OUT.brightRed("检测到重复名称的 class ").brightYellow("[" + aClass.getName() + "] ").blue("[" + clazz.getName() + "]").brightRed(" 可能会导致根据名称调用时意义不明确 !!! 建议修改 !!!").ln();
                }
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
        return APPLICATION_CONTEXT.getBean(getImplClassOrSelf(c));
    }

    /**
     * <p>fixTable.</p>
     */
    public static void fixTable() {
        try (var conn = SQLRunner.getConnection()) {
            var dm = conn.getMetaData();
            Ansi.OUT.magenta("数据源连接成功 : 类型 [" + dm.getDatabaseProductName() + "]  版本 [" + dm.getDatabaseProductVersion() + "]").ln();
        } catch (Exception e) {
            SQLRunnerExceptionHandler.sqlExceptionHandler(e);
            return;
        }
        if (ScxConfig.fixTable()) {
            Ansi.OUT.magenta("修复数据表中...").ln();
            var noNeedFix = new AtomicBoolean(true);
            SCX_BEAN_CLASS_NAME_MAPPING.forEach((k, v) -> {
                if (v.isAnnotationPresent(ScxModel.class) && !v.isInterface()) {
                    try {
                        AtomicBoolean mayBeCovered = new AtomicBoolean(false);
                        Class<?> fixTableClass = v;
                        for (Map.Entry<Class<?>, Class<?>> entry : MUST_HAVE_IMPL_MAPPING.entrySet()) {
                            Class<?> key = entry.getKey();
                            Class<?> value = entry.getValue();
                            if (key.isAssignableFrom(v)) {
                                mayBeCovered.set(true);
                                fixTableClass = value;
                                break;
                            }
                        }
                        if (SQLHelper.fixTable(fixTableClass) != FixTableResult.NO_NEED_TO_FIX) {
                            noNeedFix.set(false);
                        }
                    } catch (Exception ignored) {

                    }
                }
            });
            if (noNeedFix.get()) {
                Ansi.OUT.magenta("没有表需要修复...").ln();
            }
        }
    }

    /**
     * 初始化 context
     */
    public static void initContext() {
        Ansi.OUT.magenta("ScxContext 初始化完成...").ln();
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
        Ansi.OUT.brightBlue(binaryHandlerID + " 连接了!!! 当前总连接数 : " + ONLINE_ITEMS.size()).ln();
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
     * @return a {@link OnlineItem} object.
     */
    public static OnlineItem getOnlineItemByWebSocket(ServerWebSocket webSocket) {
        var binaryHandlerID = webSocket.binaryHandlerID();
        return ONLINE_ITEMS.stream().filter(u -> u.webSocket.binaryHandlerID().equals(binaryHandlerID)).findAny().orElse(null);
    }

    /**
     * 根据用户名获取所有的在线对象
     *
     * @param username a {@link java.lang.String} object.
     * @return a {@link OnlineItem} object.
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
