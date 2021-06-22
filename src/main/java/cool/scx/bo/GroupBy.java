package cool.scx.bo;

import java.util.HashSet;
import java.util.Set;

/**
 * 分组
 */
public class GroupBy {

    /**
     * 分组字段列表
     */
    public Set<String> groupByList = new HashSet<>();

    public GroupBy() {

    }

    public GroupBy(String fieldName) {
        add(fieldName);
    }

    /**
     * 添加一个 分组字段
     *
     * @param fieldName 分组字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @return 本身, 方便链式调用
     */
    public GroupBy add(String fieldName) {
        groupByList.add(fieldName);
        return this;
    }

}
