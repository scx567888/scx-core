package cool.scx.util;

import cool.scx.annotation.*;

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

}
