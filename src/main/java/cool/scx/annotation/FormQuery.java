package cool.scx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 从 query 获取参数
 *
 * @author 司昌旭
 * @version 0.5.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormQuery {
    String value() default "";

    /**
     * 将 查询参数聚合
     *
     * @return q
     */
    boolean merge() default false;
}
