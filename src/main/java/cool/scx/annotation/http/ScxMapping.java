package cool.scx.annotation.http;

import cool.scx.enumeration.CheckLoginType;
import cool.scx.enumeration.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>ScxMapping class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScxMapping {
    boolean unCheckedPerms() default false;

    CheckLoginType checkedLogin() default CheckLoginType.None;

    String value() default "";

    boolean useMethodNameAsUrl() default true;

    RequestMethod[] method() default {RequestMethod.GET, RequestMethod.POST};
}
