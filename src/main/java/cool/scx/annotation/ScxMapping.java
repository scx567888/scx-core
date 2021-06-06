package cool.scx.annotation;

import cool.scx.enumeration.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ScxMapping
 * 具体 http 请求映射
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScxMapping {

    /**
     * 不校验权限
     *
     * @return 权限
     */
    boolean checkedPerms() default false;

    /**
     * 检查登录
     *
     * @return 检查登录的类型
     */
    boolean checkedLogin() default false;

    /**
     * 映射的路径
     *
     * @return 路径
     */
    String value() default "";

    /**
     * 是否使用方法名称作为 mapping 名称
     * 如 方法名为  getUserList
     * 则 api为 api/user/getUserList
     *
     * @return 标识
     */
    boolean useMethodNameAsUrl() default true;

    /**
     * 请求标识 默认只支持 get 请求
     *
     * @return 方法
     */
    Method[] method() default {Method.GET};

}
