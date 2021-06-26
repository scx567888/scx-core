package cool.scx.bo;

import cool.scx.util.Ansi;

import java.util.HashSet;
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
    public final Set<String> groupByList = new HashSet<>();

    /**
     * 一个实体类的 class 对象 当不为空时可以对传进来的参数进行数据校验
     */
    private final Class<?> entityClass;

    /**
     * OrderBy 对象无参构造 只会对数据进行重复校验
     */
    public GroupBy() {
        this.entityClass = null;
    }

    /**
     * 创建一个 OrderBy 对象 (添加排序字段时会根据 entityClass 校验数据)
     */
    public GroupBy(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 添加一个 分组字段
     *
     * @param fieldName 分组字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @return 本身, 方便链式调用
     */
    public GroupBy add(final String fieldName) {
        if (checkGroupByColumn(fieldName) && !groupByList.add(fieldName)) {
            Ansi.OUT.brightRed("已跳过添加过相同的 GroupBy 字段 , 内容是: " + fieldName + " !!!").ln();
        }
        return this;
    }

    /**
     * 检查 OrderByBody <br>
     * 当 entityClass 存在时会先校验 orderByColumn 是否存在于 实体类中的字段<br>
     * 当 entityClass 不存在时则不会校验
     *
     * @return 检查的结果
     */
    private boolean checkGroupByColumn(final String groupByColumn) {
        if (entityClass != null) {
            try {
                entityClass.getField(groupByColumn);
            } catch (NoSuchFieldException e) {
                Ansi.OUT.brightRed(groupByColumn + " 不存在于 " + entityClass + " 的 field 内 , 请检查 GroupBy 字段是否正确 或使用 GroupBy 的无参构造创建对象 以忽略对字段的校验!!!").ln();
                return false;
            }
        }
        return true;
    }

}