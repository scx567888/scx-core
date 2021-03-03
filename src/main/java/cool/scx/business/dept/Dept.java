package cool.scx.business.dept;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.dao.annotation.Column;
import cool.scx.dao.annotation.NoColumn;
import cool.scx.dao.annotation.ScxModel;
import cool.scx.dao.BaseModel;

/**
 * 部门
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class Dept extends BaseModel {

    public String deptName;//部门名称

    public String perm;//部门权限

    public Integer level;//部门级别
    @Column(notNull = true, defaultValue = "0", needIndex = true)
    @JsonIgnore
    public Integer modelOrder;//排序

    @NoColumn
    public Long parentId = 0L;//父id 用作构建树形结构

    public String parentStr;//父字符串
}
