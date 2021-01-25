package cool.scx.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;

import java.util.Date;

public abstract class BaseModel {

    @Column(primaryKey = true, noInsert = true, noUpdate = true, autoIncrement = true)
    public Long id;

    @Column(noInsert = true, noUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", onUpdateValue = "CURRENT_TIMESTAMP", needIndex = true)
    public Date updateDate; // 修改时间

    @Column(noInsert = true, noUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", needIndex = true)
    public Date createDate; // 创建时间

    @Column(noInsert = true, notNull = true, defaultValue = "false")
    @JsonIgnore
    public Boolean isDeleted = false;//删除状态  false 未删除 true已删除

    @Column(notNull = true, defaultValue = "0", needIndex = true)
    @JsonIgnore
    public Integer modelOrder;//排序
}
