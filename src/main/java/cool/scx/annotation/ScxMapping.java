package cool.scx.annotation;

import cool.scx.enumeration.HttpMethod;
import cool.scx.enumeration.ReturnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScxMapping {
    boolean unCheckedPerms() default false;

    String value() default "";

    boolean useMethodNameAsUrl() default true;

    ReturnType returnType() default ReturnType.AUTO;

    HttpMethod[] httpMethod() default {HttpMethod.POST};
}
