package cool.scx._test.car;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.List;

@ScxModel(tablePrefix = "test")
public class Car extends BaseModel {
    public String name;
    public List<Long> test1;
    public List<String> tags;
}
