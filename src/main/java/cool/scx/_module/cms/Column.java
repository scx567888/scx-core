package cool.scx._module.cms;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * <p>Column class.</p>
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "cms")
public class Column extends BaseModel {

    /**
     * 栏目名称
     */
    public String columnName;

    /**
     * 栏目路径
     */
    public String columnPath;

}
