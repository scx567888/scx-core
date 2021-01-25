package cool.scx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库对象 不映射 注解
 * 添加此注解的 字段 会在转换为数据库字段时被忽略掉
 * 如果只是想在添加或者修改时不进行映射 请考虑使用 NoInsert 和 NoUpdate 注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoColumn {

}
