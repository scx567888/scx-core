package cool.scx.web.handler;

import cool.scx.annotation.ScxMapping;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.Ansi;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 处理 scxMapping 处理器
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public class ScxMappingHandlerRegister {

    private static final List<ScxMappingHandler> SCX_MAPPING_HANDLER_LIST = new ArrayList<ScxMappingHandler>();

    /**
     * 扫描所有被 ScxMapping注解标记的方法 并封装为 ScxMappingHandler.</p>
     */
    public static void ScanScxMappingHandlers() {
        SCX_MAPPING_HANDLER_LIST.clear();
        ScxModuleHandler.iterateClass(clazz -> {
            if (clazz.isAnnotationPresent(ScxMapping.class)) {
                for (var method : clazz.getMethods()) {
                    method.setAccessible(true);
                    if (method.isAnnotationPresent(ScxMapping.class)) {
                        //现根据 注解 和 方法等创建一个路由
                        var s = new ScxMappingHandler(clazz, method);
                        //此处校验路由是否已经存在
                        var b = checkRouteExists(s);
                        if (!b) {
                            SCX_MAPPING_HANDLER_LIST.add(s);
                        }
                    }
                }
            }
            return true;
        });
    }

    /**
     * 获取所有被ScxMapping注解标记的方法的 handler
     *
     * @return 所有 handler
     */
    public static List<ScxMappingHandler> getAllScxMappingHandler() {
        return SCX_MAPPING_HANDLER_LIST;
    }

    /**
     * <p>register.</p>
     *
     * @param router a {@link io.vertx.ext.web.Router} object
     */
    public static void register(Router router) {
        ScanScxMappingHandlers();
        //此处排序的意义在于将 需要正则表达式匹配的 放在最后 防止匹配错误
        SCX_MAPPING_HANDLER_LIST.stream().sorted(Comparator.comparing(s -> s.order)).forEachOrdered(c -> {
            var route = router.route(c.url);
            c.httpMethods.forEach(route::method);
            route.blockingHandler(c);
        });
    }


    /**
     * 校验路由是否已经存在
     *
     * @param handler h
     * @return true 为存在 false 为不存在
     */
    private static boolean checkRouteExists(ScxMappingHandler handler) {
        for (var a : SCX_MAPPING_HANDLER_LIST) {
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
