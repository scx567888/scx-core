package cool.scx.base;

import cool.scx.annotation.Column;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 最基本的 model 包含最基础的元数据
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public abstract class BaseModel implements Serializable {

    /**
     * id
     */
    @Column(primaryKey = true, noInsert = true, noUpdate = true, autoIncrement = true)
    public Long id;

    /**
     * 修改时间
     */
    @Column(noInsert = true, noUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", onUpdateValue = "CURRENT_TIMESTAMP", needIndex = true)
    public LocalDateTime updateDate;

    /**
     * 创建时间
     */
    @Column(noInsert = true, noUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", needIndex = true)
    public LocalDateTime createDate;

    /**
     * 删除状态  false 未删除 true 已删除
     */
    @Column(noInsert = true, notNull = true, defaultValue = "false")
    public Boolean isDeleted;

}
