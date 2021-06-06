package cool.scx._core.notice;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.List;

/**
 * Notice
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class Notice extends BaseModel {

    public String title;

    public String content;

    /**
     * 发送的类型
     */
    public int sendType;

    /**
     * 接受的用户 id 集合
     */
    public List<Long> userIds;

    /**
     * 接受的角色 id 集合
     */
    public List<Long> roleIds;

    /**
     * 接受的部门 id 集合
     */
    public List<Long> deptIds;

}
