package cool.scx.web.handler;

import cool.scx.config.ScxConfig;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * 静态文件 处理器
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public class StaticHandlerRegister {

    /**
     * <p>register.</p>
     *
     * @param router a {@link io.vertx.ext.web.Router} object
     */
    public static void register(Router router) {
        router.route(ScxConfig.cmsResourceHttpUrl())
                .handler(StaticHandler.create()
                        .setAllowRootFileSystemAccess(true)
                        .setWebRoot(ScxConfig.cmsResourceLocations().getPath()));
    }
}
