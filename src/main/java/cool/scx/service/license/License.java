package cool.scx.service.license;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 项目 license
 */
@ScxModel(tablePrefix = "core")
public class License extends BaseModel {
    public Boolean flag;//表示是否被强制禁用

    public String lastTime;//最后一次license 正确校验的时间
}
