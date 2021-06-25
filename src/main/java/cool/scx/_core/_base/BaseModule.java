package cool.scx._core._base;

import cool.scx.ScxModule;

/**
 * 基础模块
 * 提供功能 : [ 通用 crud , 基础文件上传 ,基本日志记录 , 基本 cms 指令 , 其他一些基本的工具类 ]
 *
 * @author 司昌旭
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
