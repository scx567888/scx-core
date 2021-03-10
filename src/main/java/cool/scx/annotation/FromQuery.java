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
public @interface FromQuery {

    /**
     * 查询参数名称 默认为空
     * 为空的情况下会将方法参数名称作为 查询参数名称
     *
     * @return 名称
     */
    String value() default "";

    /**
     * 将 查询参数聚合
     *
     * @return q
     */
    boolean merge() default false;
}
