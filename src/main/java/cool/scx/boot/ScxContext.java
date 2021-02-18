package cool.scx.boot;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxModel;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseDao;
import cool.scx.base.SQLRunner;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.enumeration.Color;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>ScxContext class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxContext {

    private static final ArrayList<SessionItem> scxSession = new ArrayList<>();
    private static final Map<Class<?>, Object> beanMapping = new HashMap<>();
    private static final Map<String, Class<?>> classNameMapping = new HashMap<>();
    private static final UserService userService;

    static {
        StringUtils.println("ScxContext 初始化中...", Color.GREEN);
        initScxContext();
        ScxPlugins.pluginsClassList.forEach(ScxContext::register);
        fixTable();
        userService = getBean(UserService.class);
    }

    /**
     * todo 检查 bean 是否存在循环依赖 如果存在直接 报错
     */
    private static void checkBeanCyclicDependency() {
//        var parameterTypes = clazz.getConstructors()[0].getGenericParameterTypes();
//        for (var type : parameterTypes) {
//            var s = clazz.getName();
//            var a = ((Class<?>) type).getName();
//            DsfCycle.addLine(s, a);
//            System.out.println(s + " " + a);
//        }
    }

    /**
     * <p>getClassByName.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.Class} object.
     */
    public static Class<?> getClassByName(String str) {
        return classNameMapping.get(str);
    }

    private static void initScxContext() {
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxService.class) || clazz.isAnnotationPresent(ScxController.class) || clazz.isAnnotationPresent(ScxModel.class)) {
                beanMapping.put(clazz, null);
                classNameMapping.put(clazz.getSimpleName().toLowerCase(), clazz);
            }
        });
        //此处校验是否有循环依赖
        checkBeanCyclicDependency();
    }


    /**
     * 根据 RoutingContext 获取当前用户
     *
     * @param ctx RoutingContext
     * @return 当前用户
     */
    public static User getCurrentUser(RoutingContext ctx) {
        String token = ctx.request().getHeader(ScxConfig.tokenKey);
        return getUserFromSessionByToken(token);
    }

    /**
     * <p>getBean.</p>
     *
     * @param c   a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> c) {
        Object o = beanMapping.get(c);
        if (o != null) {
            return (T) o;
        } else {
            var constructors = c.getConstructors();
            for (var constructor : constructors) {
                var genericParameterTypes = constructor.getGenericParameterTypes();
                var p = new Object[genericParameterTypes.length];
                for (int i = 0; i < genericParameterTypes.length; i++) {
                    p[i] = getBean((Class<?>) genericParameterTypes[i]);
                }
                try {
                    o = constructor.newInstance(p);
                    beanMapping.put(c, o);
                    return (T) o;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        throw new RuntimeException("无法创建" + c.getName() + "对应的 bean");
    }

    /**
     * <p>register.</p>
     *
     * @param c a {@link java.lang.Class} object.
     */
    public static void register(Class<?> c) {
        beanMapping.put(c, null);
    }

    /**
     * <p>fixTable.</p>
     */
    public static void fixTable() {
        if (SQLRunner.testConnection()) {
            StringUtils.println("修复数据表中...", Color.MAGENTA);
            if (ScxConfig.fixTable) {
                beanMapping.forEach((k, v) -> {
                    if (k.isAnnotationPresent(ScxModel.class)) {
                        try {
                            BaseDao.fixTable(k);
                        } catch (Exception ignored) {

                        }
                    }
                });
            }
        }
    }

    /**
     * <p>init.</p>
     */
    public static void init() {
        StringUtils.println("ScxContext 初始化完成...", Color.GREEN);
    }

    /**
     * <p>logoutUser.</p>
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object.
     */
    public static void logoutUser(RoutingContext ctx) {
        var token = ctx.request().getHeader(ScxConfig.tokenKey);
        scxSession.removeIf(i -> i.token.equals(token));
    }

    public static void addUserToSession(String token, String username) {
        var sessionItem = scxSession.stream().filter(u -> u.username.equals(username)).findAny().orElse(null);
        if (sessionItem == null) {
            scxSession.add(new SessionItem(token, username, new ArrayList<>()));
        } else {
            sessionItem.username = username;
            sessionItem.token = token;
        }
    }

    public static User getUserFromSessionByToken(String token) {
        var sessionItem = scxSession.stream().filter(u -> u.token.equals(token)).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        return userService.findByUsername(sessionItem.username);
    }

    private static class SessionItem {
        public String token;//唯一的
        public String username;//唯一的
        public ArrayList<Object> userSessionList;

        public SessionItem(String _token, String _username, ArrayList<Object> _userSessionList) {
            token = _token;
            username = _username;
            userSessionList = _userSessionList;
        }
    }

}
