package cool.scx.boot;

import cool.scx.annotation.ScxModel;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseDao;
import cool.scx.base.BaseService;
import cool.scx.base.SQLRunner;
import cool.scx.business.user.User;
import cool.scx.business.user.UserService;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ScxContext {

    private static final ArrayList<SessionItem> scxSession = new ArrayList<>();
    private static final AnnotationConfigApplicationContext applicationContext;
    private static final UserService userService;
    private static final Map<String, Class<?>> baseModelClassCache;
    private static final Map<String, BaseService<?>> baseServiceCache;

    static {
        StringUtils.println("ScxContext 初始化中...", StringUtils.Color.GREEN);
        applicationContext = new AnnotationConfigApplicationContext(PackageUtils.getBasePackages());
        ScxPlugins.pluginsClassList.forEach(applicationContext::register);
        userService = getBean(UserService.class);
        baseModelClassCache = initBaseModelClassCache();
        baseServiceCache = initBaseServiceCache();
        fixTable();
    }

    public static Class<?> getBaseModelClassByName(String str) {
        return baseModelClassCache.get(str);
    }

    public static BaseService<?> getBaseServiceByName(String str) {
        return baseServiceCache.get(str);
    }

    private static Map<String, Class<?>> initBaseModelClassCache() {
        var tempBaseModelClassCache = new HashMap<String, Class<?>>();
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxModel.class)) {
                tempBaseModelClassCache.put(clazz.getSimpleName().toLowerCase(), clazz);
            }
        });
        return tempBaseModelClassCache;
    }

    private static Map<String, BaseService<?>> initBaseServiceCache() {
        var tempBaseServiceCache = new HashMap<String, BaseService<?>>();
        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (clazz.isAnnotationPresent(ScxService.class)) {
                tempBaseServiceCache.put(clazz.getSimpleName().toLowerCase(), (BaseService<?>) getBean(clazz));
            }
        });
        return tempBaseServiceCache;
    }

    public static void logoutUser() {
        //var token = getHttpRequest().getHeader(ScxContext.tokenKey);
        var token = "getHttpRequest().getHeader(ScxContext.tokenKey);";
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

    public static User getCurrentUser() {
        //var token = getHttpRequest().getHeader(ScxContext.tokenKey);
        var token = "getHttpRequest().getHeader(ScxContext.tokenKey)";
        return getUserFromSessionByToken(token);
    }

    public static User getUserFromSessionByToken(String token) {
        var sessionItem = scxSession.stream().filter(u -> u.token.equals(token)).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        return userService.findByUsername(sessionItem.username);
    }

    public static <T> T getBean(Class<T> c) {
        return applicationContext.getBean(c);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    public static void fixTable() {
        if (SQLRunner.testConnection()) {
            StringUtils.println("修复数据表中...", StringUtils.Color.MAGENTA);
            if (ScxConfig.fixTable) {
                PackageUtils.scanPackage((clazz) -> {
                    if (clazz.isAnnotationPresent(ScxModel.class)) {
                        try {
                            BaseDao.fixTable(clazz);
                        } catch (Exception ignored) {

                        }
                    }
                });
            }
        }
    }

    public static UserService getUserService() {
        return userService;
    }

    public static void init() {
        StringUtils.println("ScxContext 初始化完成...", StringUtils.Color.GREEN);
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
