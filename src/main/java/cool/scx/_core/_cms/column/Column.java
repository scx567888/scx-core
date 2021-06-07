package cool.scx._core._cms.column;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * <p>Column class.</p>
 *
 * @author 司昌旭
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
