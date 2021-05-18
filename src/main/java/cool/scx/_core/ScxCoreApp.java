package cool.scx._core;

import cool.scx.boot.ScxApp;
import cool.scx.boot.ScxModule;

/**
 * 核心模块启动类
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class ScxCoreApp implements ScxModule {

    /**
     * 核心启动方法
     *
     * @param args 外部参数
     */
    public static void main(String[] args) {
        ScxApp.run(new ScxCoreApp(), args);
    }
}