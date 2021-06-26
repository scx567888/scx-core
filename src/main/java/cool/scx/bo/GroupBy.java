package cool.scx.bo;

import cool.scx.util.Ansi;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 分组
 *
 * @author 司昌旭
 * @version 1.2.0
 */
public class GroupBy {

    /**
     * 分组字段列表
     */
    public final Set<String> groupByList = new LinkedHashSet<>();

    /**
     * 创建一个 OrderBy 对象
     */
    public GroupBy() {

    }

    /**
     * 创建一个 OrderBy 对象
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
    public GroupBy add(final String fieldName) {
        if (!groupByList.add(fieldName)) {
            Ansi.OUT.brightRed("已跳过添加过相同的 GroupBy 字段 , 内容是: " + fieldName + " !!!").ln();
        }
        return this;
    }

}
