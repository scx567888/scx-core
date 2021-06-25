package cool.scx.bo;

import cool.scx.base.BaseModel;
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
    private Class<? extends BaseModel> entityClass = null;

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
     * 创建一个 OrderBy 对象 (添加排序字段时会根据 entityClass 校验数据)
     */
    public GroupBy(String fieldName, Class<? extends BaseModel> entityClass) {
        this(entityClass);
        add(fieldName);
    }

    /**
     * 创建一个 OrderBy 对象 (添加排序字段时会根据 entityClass 校验数据)
     */
    public GroupBy(Class<? extends BaseModel> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 添加一个 分组字段
     *
     * @param fieldName 分组字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @return 本身, 方便链式调用
     */
    public GroupBy add(final String fieldName) {
        boolean b = checkGroupByColumn(fieldName);
        if (b) {
            groupByList.add(fieldName);
        }
        return this;
    }

    /**
     * 添加一个 分组字段
     *
     * @param fieldName 分组字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @return 本身, 方便链式调用
     */
    public GroupBy forceAdd(final String fieldName) {
        groupByList.add(fieldName);
        return this;
    }

    /**
     * 检查 OrderByBody 此处做两个校验<br>
     * 第一个是 当 entityClass 存在时会先校验 orderByColumn 是否存在于 实体类中的字段<br>
     * 当 entityClass 不存在时则不会校验
     * <br>
     * 第二个是对 数据重复进行校验 理论上 orderBy 不应该存在相同的字段
     *
     * @return 检查的结果 只有为 true 时才会向列表中添加
     */
    private boolean checkGroupByColumn(final String groupByColumn) {
        //先检查 orderByColumn 是不是存在于类中的
        if (entityClass != null) {
            try {
                entityClass.getField(groupByColumn);
            } catch (NoSuchFieldException e) {
                Ansi.OUT.brightRed(groupByColumn + " 不存在于 " + entityClass + " 的 field 内 , 请检查 GroupBy 字段是否正确 或使用 GroupBy 的无参构造创建对象 以忽略对字段的校验!!!").ln();
                return false;
            }
        }

        boolean contains = groupByList.contains(groupByColumn);
        if (!contains) {
            return true;
        } else {
            Ansi.OUT.brightRed("已经添加过相同的 GroupBy 字段 , 内容是: " + groupByColumn + " , 若要强行覆盖请使用 forceAdd !!!").ln();
            return false;
        }

    }

}
