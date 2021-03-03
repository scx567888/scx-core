package cool.scx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>PathParam class.</p>
 *
 * @author 司昌旭
 * @version 0.5.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromPath {
    String value() default "";

    /**
     * 聚合参数
     *
     * @return 将 path 聚合为一个对象
     */
    boolean merge() default false;
}
