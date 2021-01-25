package cool.scx.service.cms;


import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

//栏目
@ScxModel(tablePrefix = "cms")
public class Column extends BaseModel {
    public String columnName;//栏目名称
    public String columnPath;//栏目路径
}
