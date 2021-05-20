package cool.scx.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ScxController
 * http 映射
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScxController {

    /**
     * 路径
     * 当和 scxMapping 配合使用时 会将
     * 此路径作为父路径 和 scxMapping 的子路径进行拼接
     *
     * @return 路径
     */
    String value() default "";

}
