package cool.scx._core.cms;

import cool.scx.BaseModel;
import cool.scx.annotation.ScxModel;

/**
 * <p>Column class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
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
