package cool.scx.util;

import cool.scx.annotation.*;
import cool.scx.module.ScxModule;

import java.util.List;

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

    @SuppressWarnings("unchecked")
    public static List<ScxModule> cast(Object o) {
        return (List<ScxModule>) o;
    }

}
