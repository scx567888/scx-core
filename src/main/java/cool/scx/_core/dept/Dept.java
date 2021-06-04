package cool.scx._core.dept;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.BaseModel;
import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;

import java.util.List;

/**
 * 部门
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class Dept extends BaseModel {

    /**
     * 部门名称
     */
    public String deptName;

    /**
     * 部门权限
     */
    public List<String> perms;

    /**
     * 排序
     */
    @Column(notNull = true, defaultValue = "0", needIndex = true)
    @JsonIgnore
    public Integer deptOrder;

    /**
     * 父id 用作构建树形结构
     */
    @NoColumn
    public Long parentId = 0L;

}
