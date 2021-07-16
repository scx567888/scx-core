package cool.scx.util;

import cool.scx.annotation.*;
import cool.scx.module.ScxModule;

import java.util.List;

/**
 * <p>ScxUtils class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class ScxUtils {

    /**
     * 拥有 scx 注解
     *
     * @param clazz class
     * @return b
     */
    public static boolean hasScxAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(ScxService.class)
                || clazz.isAnnotationPresent(ScxMapping.class)
                || clazz.isAnnotationPresent(ScxModel.class)
                || clazz.isAnnotationPresent(ScxTemplateDirective.class)
                || clazz.isAnnotationPresent(ScxWebSocketRoute.class);
    }

    /**
     * <p>cast.</p>
     *
     * @param o a {@link java.lang.Object} object
     * @return a {@link java.util.List} object
     */
    @SuppressWarnings("unchecked")
    public static List<ScxModule> cast(Object o) {
        return (List<ScxModule>) o;
    }

}
