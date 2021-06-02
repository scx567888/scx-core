package cool.scx._test.teacher;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 教师
 */
@ScxModel(tablePrefix = "test")
public class Teacher extends BaseModel {
    public String teacherName;
    public Integer teacherAge;
}
