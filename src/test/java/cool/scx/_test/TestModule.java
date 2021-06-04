package cool.scx._test;

import cool.scx.ScxApp;
import cool.scx.ScxModule;
import cool.scx._core.CoreModule;

public class TestModule implements ScxModule {

    public static void main(String[] args) {
        ScxApp.run(new ScxModule[]{new CoreModule(), new TestModule()}, args);
    }

}
