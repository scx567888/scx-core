package cool.scx;

/**
 * SCX 模块接口 , 自定义模块必须实现此接口
 * <p>
 * 当自定义的模块实现此接口之后 , 会根据 自定义模块的 根 package 进行扫描 , 所以功能代码请放在自定义模块的包或子包下
 * <p>
 * 生命周期请参阅方法说明
 *
 * @author scx567888
 * @version 1.1.2
 */
public interface ScxModule {

    /**
     * 配置文件加载完成调用
     */
    default void init() {

    }

    /**
     * 核心模块初始化完成调用
     */
    default void start() {

    }

    /**
     * 项目停止或结束时调用
     */
    default void stop() {

    }

    /**
     * appKey 密码 (强烈建议您重写此方法)
     *
     * @return app
     */
    default String appKey() {
        return null;
    }

}
