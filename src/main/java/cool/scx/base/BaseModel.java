package cool.scx.base;

import cool.scx.annotation.Column;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 最基本的 model 包含最基础的元数据
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class BaseModel implements Serializable {

    /**
     * id
     */
    @Column(primaryKey = true, excludeOnInsert = true, excludeOnUpdate = true, autoIncrement = true)
    public Long id;

    /**
     * 修改时间
     */
    @Column(excludeOnInsert = true, excludeOnUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", onUpdateValue = "CURRENT_TIMESTAMP", needIndex = true)
    public LocalDateTime updateDate;

    /**
     * 创建时间
     */
    @Column(excludeOnInsert = true, excludeOnUpdate = true, notNull = true, defaultValue = "CURRENT_TIMESTAMP", needIndex = true)
    public LocalDateTime createDate;

    /**
     * 墓碑
     * <p>
     * 用于标识逻辑删除的状态
     */
    @Column(excludeOnInsert = true, notNull = true, defaultValue = "false")
    public Boolean tombstone;

}
