package cool.scx.bo;

import java.util.HashSet;
import java.util.Set;

/**
 * 分组
 * todo 数据校验
 *
 * @author 司昌旭
 * @version 1.2.0
 */
public class GroupBy {

    /**
     * 分组字段列表
     */
    public final Set<String> groupByList = new HashSet<>();

    /**
     * <p>Constructor for GroupBy.</p>
     */
    public GroupBy() {

    }

    /**
     * <p>Constructor for GroupBy.</p>
     *
     * @param fieldName a {@link java.lang.String} object
     */
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
