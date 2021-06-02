package cool.scx._test.personnel;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 人员
 */
@ScxModel(tablePrefix = "test")
public class Personnel  extends BaseModel {
    public String name;
    public Integer age;
    public String gender;
    public String idCard;
    public String address;
    public String deptName;
    public String positionName;
    public String avatar;
    public String remarks;
}
