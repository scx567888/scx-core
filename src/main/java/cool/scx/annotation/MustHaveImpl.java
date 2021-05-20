package cool.scx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注此注解的类或接口在 ScxModule 中必须有一个实现
 * 若有多个实现 则根据 ScxModule 加载的顺序取最后一个
 *
 * @author 司昌旭
 * @version 1.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MustHaveImpl {

}
