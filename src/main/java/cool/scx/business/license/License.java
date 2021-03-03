package cool.scx.business.license;

import cool.scx.dao.BaseModel;
import cool.scx.dao.annotation.ScxModel;

/**
 * 项目 license
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class License extends BaseModel {
    public Boolean flag;//表示是否被强制禁用

    public String lastTime;//最后一次license 正确校验的时间
}
