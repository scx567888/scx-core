package cool.scx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 从 body 获取参数
 * value 为空时 将转换整个对象
 * value 不为空时会先将 body 转换 map 对象 然后根据 value 进行分层获取
 * value 格式可以为 userList.name , name , car.color 等
 *
 * @author 司昌旭
 * @version 0.5.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromBody {
    String value() default "";
}
