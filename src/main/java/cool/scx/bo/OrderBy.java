package cool.scx.bo;

import cool.scx.base.BaseModel;
import cool.scx.enumeration.OrderByType;
import cool.scx.util.Ansi;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 排序
 *
 * @author 司昌旭
 * @version 1.2.0
 */
public class OrderBy {

    /**
     * 存储排序的字段
     */
    public final Set<OrderByBody> orderByList = new HashSet<>();

    /**
     * 一个实体类的 class 对象 当不为空时可以对传进来的参数进行数据校验
     */
    private final Class<?> entityClass;

    /**
     * 创建一个 OrderBy 对象 (添加排序字段时不会校验数据)
     */
    public OrderBy() {
        this.entityClass = null;
    }

    /**
     * 创建一个 OrderBy 对象 (添加排序字段时会根据 entityClass 校验数据)
     */
    public OrderBy(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * <p>Constructor for OrderBy.</p>
     *
     * @param orderByColumn a {@link java.lang.String} object
     * @param orderByType   a {@link cool.scx.enumeration.OrderByType} object
     */
    public OrderBy(final String orderByColumn, final OrderByType orderByType) {
        this.entityClass = null;
        add(orderByColumn, orderByType);
    }

    /**
     * <p>Constructor for OrderBy.</p>
     *
     * @param orderByColumn a {@link java.lang.String} object
     * @param orderByType   a {@link cool.scx.enumeration.OrderByType} object
     */
    public OrderBy(final String orderByColumn, final OrderByType orderByType, final Class<? extends BaseModel> entityClass) {
        this.entityClass = entityClass;
        add(orderByColumn, orderByType);
    }

    /**
     * 添加一个排序字段
     *
     * @param orderByColumn 排序字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @param orderByType   排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public OrderBy add(final String orderByColumn, final OrderByType orderByType) {
        var orderByBody = new OrderByBody(orderByColumn.trim(), orderByType, false);
        if (checkOrderByBody(orderByBody)) {
            orderByList.add(orderByBody);
        }
        return this;
    }

    /**
     * 添加一个排序 SQL
     *
     * @param orderByColumn 排序 SQL ( SQL 表达式 )
     * @param orderByType   排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public OrderBy addSQL(final String orderByColumn, final OrderByType orderByType) {
        var orderByBody = new OrderByBody(orderByColumn.trim(), orderByType, true);
        if (checkOrderByBody(orderByBody)) {
            orderByList.add(orderByBody);
        }
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
    private boolean checkOrderByBody(OrderByBody orderByBody) {
        //先检查 orderByColumn 是不是存在于类中的
        if (entityClass != null && !orderByBody.isSQL) {
            try {
                entityClass.getField(orderByBody.orderByColumn);
            } catch (NoSuchFieldException e) {
                Ansi.OUT.brightRed(orderByBody.orderByColumn + " 不存在于 " + entityClass + " 的 field 内 , 请检查 OrderBy 字段是否正确 或使用 OrderBy 的无参构造创建对象 以忽略对字段的校验!!!").ln();
                return false;
            }
        }

        boolean contains = orderByList.contains(orderByBody);
        if (contains) {
            Ansi.OUT.brightRed("检测到相同的 OrderBy 字段 , 名称为: " + orderByBody.orderByColumn + " , 内容已覆盖 !!!").ln();
        }
        return true;

    }

    /**
     * OrderBy 封装体
     */
    public static class OrderByBody {

        /**
         * 字段名称 (注意不是数据库名称)
         */
        public final String orderByColumn;

        /**
         * 类型
         */
        public final OrderByType orderByType;

        /**
         * 是否为 sql (因为 order by 可以使用表达式 所以这里进行判断)
         */
        public final boolean isSQL;

        public OrderByBody(String orderByColumn, OrderByType orderByType, boolean isSQL) {
            this.orderByColumn = orderByColumn;
            this.orderByType = orderByType;
            this.isSQL = isSQL;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderByBody that = (OrderByBody) o;
            return isSQL == that.isSQL && Objects.equals(orderByColumn, that.orderByColumn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderByColumn, isSQL);
        }
    }

}
