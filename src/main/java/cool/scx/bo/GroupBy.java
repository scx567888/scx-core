package cool.scx.bo;

import java.util.HashSet;
import java.util.Set;

/**
 * 分组
 */
public class GroupBy {
    /**
     * 自定义分组 SQL 添加
     */
    public Set<String> groupByList = new HashSet<>();

    public GroupBy() {

    }

    public void add(String groupByColumn) {
        groupByList.add(groupByColumn);
    }
}
