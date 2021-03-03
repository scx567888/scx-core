package cool.scx.dao.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>ScxModel class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScxModel {
    /**
     * 表名称
     *
     * @return 表全限定名称
     */
    String tableName() default "";

    /**
     * 表名称前缀
     *
     * @return 表前缀
     */
    String tablePrefix() default "";
}
