package cool.scx._test;

import cool.scx.ScxApp;
import cool.scx.ScxModule;
import cool.scx._module.auth.AuthModule;
import cool.scx._module.base.BaseModule;
import cool.scx._module.cms.CmsModule;

public class TestModule implements ScxModule {

    public static void main(String[] args) {
        //引入模块
        ScxModule[] modules = {
                new BaseModule(),
                new CmsModule(),
                new AuthModule(),
                new TestModule()
        };
        //运行项目
        ScxApp.run(modules, args);
    }

    @Override
    public String appKey() {
        return "H8QS91GcuNGP9735";
    }

}
