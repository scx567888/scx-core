package cool.scx.base;

/**
 * 模块接口 所有需要加载的模块都应该实现此接口
 * 当自定义的模块继承此接口之后
 * 便会根据 当前模块的 根 package 进行扫描 所以自定义模块下的所有 类 需放在 自定义模块的包或子包下
 * <p>
 * 生命周期起始 onStart
 * 生命周期结束 onEnd
 *
 * @author 司昌旭
 * @version 1.1.2
 */
public interface BaseModule {

    /**
     * <p>onStart.</p>
     */
    default void start() {
        System.out.println(" onStart !!!");
    }

    /**
     * <p>onEnd.</p>
     */
    default void stop() {
        var aClass = this.getClass();
        System.out.println(aClass.getName() + " onEnd !!!");
    }

}
