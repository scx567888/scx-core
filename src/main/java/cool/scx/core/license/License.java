package cool.scx.core.license;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

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
