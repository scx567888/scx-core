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

import java.util.HashMap;
import java.util.Map;

public final class ScxContext {

    private static final Map<Class<?>, Object> beanMapping = new HashMap<>();
    private static final Map<String, Class<?>> classNameMapping = new HashMap<>();

    static {
        StringUtils.println("ScxContext 初始化中...", Color.GREEN);
        initScxContext();
        ScxPlugins.pluginsClassList.forEach(ScxContext::register);
        fixTable();
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

    public static void logoutUser(RoutingContext ctx) {
        ctx.session().remove(ScxConfig.tokenKey);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public static User getCurrentUser(RoutingContext ctx) {
        Long currentUserId = ctx.session().get(ScxConfig.tokenKey);
        return getBean(UserService.class).getById(currentUserId);
    }

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

    public static void register(Class<?> c) {
        beanMapping.put(c, null);
    }

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

    public static void init() {
        StringUtils.println("ScxContext 初始化完成...", Color.GREEN);
    }

}
