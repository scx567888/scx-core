package cool.scx._test.student;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 学生
 */
@ScxModel(tablePrefix = "test")
public class Student extends BaseModel {
    public String studentName;
    public Integer studentAge;
}
