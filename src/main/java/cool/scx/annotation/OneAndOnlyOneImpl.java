package cool.scx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 有且只能有一个实现
 * 若有多个实现 根据 ScxModule 加载的顺序取最后一个
 *
 * @author scx56
 * @version $Id: $Id
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneAndOnlyOneImpl {

}
