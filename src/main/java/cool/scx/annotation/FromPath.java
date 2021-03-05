package cool.scx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FromPath
 * 获取 路径参数
 *
 * @author 司昌旭
 * @version 0.5.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromPath {

    /**
     * 路径参数名称 默认为空
     * 为空的情况下会将参数名称作为 路径参数名称
     *
     * @return 名称
     */
    String value() default "";

    /**
     * 聚合参数
     *
     * @return 将 path 聚合为一个对象
     */
    boolean merge() default false;
}
