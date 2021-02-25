package cool.scx.annotation.websocket;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>ScxWebSocketController class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScxWebSocketController {
    String value();
}
