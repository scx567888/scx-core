package cool.scx.business.cms;


import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

//栏目
/**
 * <p>Column class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "cms")
public class Column extends BaseModel {
    public String columnName;//栏目名称
    public String columnPath;//栏目路径
}
