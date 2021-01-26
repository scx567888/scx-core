package cool.scx.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;

import java.util.Date;

/**
 * 最基本的 model 包含最基础的元数据
 */
public abstract class BaseModel {

    /**
     * id
     */
    @Column(primaryKey = true, noInsert = true, noUpdate = true, autoIncrement = true)
    public Long id;

    /**
     * 修改时间
     */
    @Column(noInsert = true, noUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", onUpdateValue = "CURRENT_TIMESTAMP", needIndex = true)
    public Date updateDate;

    /**
     * 创建时间
     */
    @Column(noInsert = true, noUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", needIndex = true)
    public Date createDate;

    /**
     * 删除状态  false 未删除 true 已删除
     */
    @Column(noInsert = true, notNull = true, defaultValue = "false")
    @JsonIgnore
    public Boolean isDeleted = false;

}
