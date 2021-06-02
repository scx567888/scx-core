package cool.scx._test;

import cool.scx._core.CoreModule;
import cool.scx.base.BaseModule;
import cool.scx.boot.ScxApp;

public class TestModule implements BaseModule {

    public static void main(String[] args) {
        ScxApp.run(new BaseModule[]{new CoreModule(), new TestModule()}, args);
    }

}
