package cool.scx.base;

import java.util.Map;

/**
 * 模块接口 所有需要加载的模块都应该实现此接口
 * 当自定义的模块继承此接口之后
 * 便会根据 当前模块的 根 package 进行扫描 所以自定义模块下的所有 类 需放在 自定义模块的包或子包下
 * <p>
 * 生命周期起始 onStart
 * 生命周期结束 onEnd
 *
 * @author scx56
 * @version $Id: $Id
 */
public interface BaseModule {

    /**
     * <p>onStart.</p>
     */
    default void onStart(Map<String, Object> configMap) {
        System.out.println(" onStart !!!");
    }

    /**
     * <p>onEnd.</p>
     */
    default void onEnd() {
        var aClass = this.getClass();
        System.out.println(aClass.getName() + " onEnd !!!");
    }

}
