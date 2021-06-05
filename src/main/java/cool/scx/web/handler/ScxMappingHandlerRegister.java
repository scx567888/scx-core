package cool.scx.web.handler;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.Ansi;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 处理 scxMapping 处理器
 */
public class ScxMappingHandlerRegister {

    public static void register(Router router) {
        var scxMappingHandlers = new ArrayList<ScxMappingHandler>();
        ScxModuleHandler.iterateClass(clazz -> {
            if (clazz.isAnnotationPresent(ScxController.class)) {
                for (var method : clazz.getMethods()) {
                    method.setAccessible(true);
                    if (method.isAnnotationPresent(ScxMapping.class)) {
                        //现根据 注解 和 方法等创建一个路由
                        var s = new ScxMappingHandler(clazz, method);
                        //此处校验路由是否已经存在
                        var b = checkRouteExists(scxMappingHandlers, s);
                        if (!b) {
                            scxMappingHandlers.add(s);
                        }
                    }
                }
            }
            return true;
        });
        //此处排序的意义在于将 需要正则表达式匹配的 放在最后 防止匹配错误
        scxMappingHandlers.stream().sorted(Comparator.comparing(s -> s.order)).forEachOrdered(c -> {
            var route = router.route(c.url);
            c.httpMethods.forEach(route::method);
            route.blockingHandler(c);
        });
    }


    /**
     * 校验路由是否已经存在
     *
     * @param list    l
     * @param handler h
     * @return true 为存在 false 为不存在
     */
    private static boolean checkRouteExists(List<ScxMappingHandler> list, ScxMappingHandler handler) {
        for (var a : list) {
            if (a.url.equals(handler.url)) {
                for (var h : handler.httpMethods) {
                    if (a.httpMethods.contains(h)) {
                        Ansi.OUT.brightMagenta("检测到重复的路由!!! " + h + " --> \"" + handler.url + "\" , 相关 class 及方法如下 ▼").ln()
                                .brightMagenta(handler.clazz.getName() + " --> " + handler.method.getName()).ln()
                                .brightMagenta(a.clazz.getName() + " --> " + a.method.getName()).ln();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
