package cool.scx.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义 模板指令
 * 使用此注解时 必须同时继承
 * BaseTemplateDirective
 *
 * @author 司昌旭
 * @version 1.0.10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScxTemplateDirective {
}
