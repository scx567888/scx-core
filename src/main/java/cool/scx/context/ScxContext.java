package cool.scx.context;

import cool.scx.ScxEventBus;
import cool.scx.config.ScxConfig;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.Ansi;
import cool.scx.util.ScxUtils;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;

/**
 * ScxContext 上下文
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class ScxContext {

    /**
     * bean 注册完成时事件名称
     */
    public static final String ON_CONTEXT_REGISTER_NAME = "onContextRegister";

    /**
     * bean 移除时事件名称
     */
    public static final String ON_CONTEXT_REMOVE_NAME = "onContextRemove";

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

    static {
        Ansi.OUT.brightBlue("ScxContext 初始化中...").ln();
        //刷新
        APPLICATION_CONTEXT.refresh();
        //模块加载时的消费者
        ScxEventBus.consumer(ScxModuleHandler.ON_SCX_MODULE_REGISTER_NAME, o -> {
            var scxModuleList = ScxUtils.cast(o);
            for (var scxModule : scxModuleList) {
                var allBean = initScxAnnotationBean(scxModule.classList);
                var beanNumber = Arrays.stream(allBean).filter(s -> s.startsWith(scxModule.basePackage)).count();
                Ansi.OUT.brightBlue("模块 [" + scxModule.moduleName + "] 共加载 " + beanNumber + " 个 Bean !!!").ln();
                if (ScxConfig.showLog()) {
                    Arrays.stream(allBean).filter(s -> s.startsWith(scxModule.basePackage)).forEach(c -> Ansi.OUT.brightYellow(c).ln());
                }
            }
            //通知其他模块 bean 注册完毕,可正常使用
            ScxEventBus.publish(ON_CONTEXT_REGISTER_NAME, scxModuleList);
        });

        //模块销毁时的消费者
        ScxEventBus.consumer(ScxModuleHandler.ON_SCX_MODULE_REMOVE_NAME, scxModule -> {

        });

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

    private static String[] initScxAnnotationBean(List<Class<?>> classList) {
        classList.stream().filter(ScxUtils::hasScxAnnotation).forEach(c -> APPLICATION_CONTEXT.registerBean(c.getName(), c));

//        for (Class<?> c : needRegisterClassList) {
//            var className = c.getSimpleName().toLowerCase();
//            var aClass = SCX_BEAN_CLASS_NAME_MAPPING.get(className);
//            if (aClass == null) {
//                SCX_BEAN_CLASS_NAME_MAPPING.put(c.getSimpleName().toLowerCase(), c);
//            } else {
//                SCX_BEAN_CLASS_NAME_MAPPING.put(c.getName(), c);
//                Ansi.OUT.brightRed("检测到重复名称的 class ").brightYellow("[" + aClass.getName() + "] ").blue("[" + c.getName() + "]").brightRed(" 可能会导致根据名称调用时意义不明确 !!! 建议修改 !!!").ln();
//            }
//        }
        return APPLICATION_CONTEXT.getBeanDefinitionNames();
    }

    /**
     * <p>scxBeanClassNameMapping.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public static Map<String, Class<?>> scxBeanClassNameMapping() {
        return SCX_BEAN_CLASS_NAME_MAPPING;
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
     * 初始化 context
     */
    public static void initContext() {
        Ansi.OUT.brightBlue("ScxContext 初始化完成...").ln();
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
