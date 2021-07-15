package cool.scx._test0;

import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.Method;

@ScxMapping("/")
public class Test0Controller {

    @ScxMapping(method = Method.GET, value = "test0")
    public String index() {
        return "test0";
    }
}
