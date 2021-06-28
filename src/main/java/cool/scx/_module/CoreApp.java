package cool.scx._module;

import cool.scx.ScxApp;
import cool.scx.ScxModule;
import cool.scx._module.auth.AuthModule;
import cool.scx._module.base.BaseModule;
import cool.scx._module.cms.CmsModule;

/**
 * 运行所有核心包提供的模块 (演示用,不要用于生产环境)
 *
 * @author 司昌旭
 * @version 1.1.11
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
