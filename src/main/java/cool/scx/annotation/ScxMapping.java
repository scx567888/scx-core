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
     * 是否使用类名(方法名)称作为 url 路径 <br>
     * 仅当 value 为 "" 时生效 <br>
     * 规则为 去除类名的 controller 后缀 (如果有) 并获取方法名称 然后转换为 短横线命名法 <br>
     * 如 类名为  UserController 方法名为  getUserList <br>
     * 则 url 为 /user/get-user-list
     *
     * @return 标识
     */
    boolean useNameAsUrl() default true;

    /**
     * 请求标识 默认只支持 get 请求
     *
     * @return 方法
     */
    Method[] method() default {Method.GET};

}
