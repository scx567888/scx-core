package cool.scx._core;

import cool.scx.ScxApp;
import cool.scx.ScxModule;
import cool.scx._core._auth.AuthModule;
import cool.scx._core._base.BaseModule;
import cool.scx._core._cms.CmsModule;

/**
 * 运行 核心模块
 */
public class CoreApp {


    /**
     * 核心启动方法
     *
     * @param args 外部参数
     */
    public static void main(String[] args) {
        ScxModule[] modules = {new BaseModule(), new CmsModule(), new AuthModule()};
        ScxApp.run(modules, args);
    }

}
