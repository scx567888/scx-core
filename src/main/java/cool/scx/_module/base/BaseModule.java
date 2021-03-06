package cool.scx._module.base;

import cool.scx.ScxModule;

/**
 * 基础模块
 * 提供功能 : [ 通用 crud , 基础文件上传 ,基本日志记录 , 基本模板指令 , 其他一些基本的工具类 ]
 *
 * @author scx567888
 * @version 1.0.10
 */
public class BaseModule implements ScxModule {

    /**
     * {@inheritDoc}
     * <p>
     * start
     */
    @Override
    public void init() {
        BaseConfig.initConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appKey() {
        return "H8QS91GcuNGP9735";
    }

}
