package cool.scx._test;

import cool.scx.ScxApp;
import cool.scx.ScxModule;
import cool.scx._core._auth.AuthModule;
import cool.scx._core._base.BaseModule;

public class TestModule implements ScxModule {

    public static void main(String[] args) {
        ScxModule[] modules = {new BaseModule(), new AuthModule(), new TestModule()};
        ScxApp.run(modules, args);
    }

}
