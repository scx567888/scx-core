package cool.scx._core.license;

import cool.scx.BaseModel;
import cool.scx.annotation.ScxModel;

/**
 * 项目 license
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class License extends BaseModel {

    /**
     * 表示是否被强制禁用
     */
    public Boolean flag;

    /**
     * 最后一次license 正确校验的时间
     * 防止时间篡改
     */
    public String lastTime;

}
