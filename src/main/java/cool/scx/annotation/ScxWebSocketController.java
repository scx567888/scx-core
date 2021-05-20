package cool.scx.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ScxWebSocketController
 * websocket 映射
 * 设置此注解的方法 必须同时 继承 BaseWebSocketController 才可以生效
 *
 * @author 司昌旭
 * @version 0.5.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScxWebSocketController {

    /**
     * websocket 映射路径
     *
     * @return 映射路径
     */
    String value();

}
