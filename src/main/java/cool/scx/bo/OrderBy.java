package cool.scx.bo;

import cool.scx.enumeration.OrderByType;
import cool.scx.util.Ansi;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序
 *
 * @author scx567888
 * @version 1.2.0
 */
public class OrderBy {

    /**
     * 存储排序的字段
     */
    public final List<OrderByBody> orderByList = new ArrayList<>();

    /**
     * 创建一个 OrderBy 对象
     */
    public OrderBy() {

    }

    /**
     * c
     *
     * @param orderByColumn a
     * @param orderByType   a
     */
    public OrderBy(final String orderByColumn, final OrderByType orderByType) {
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
        orderByList.add(new OrderByBody(orderByColumn.trim(), orderByType, false));
        return this;
    }

    /**
     * 添加一个排序字段
     *
     * @param orderByColumn 排序字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @param orderByStr    排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public OrderBy add(final String orderByColumn, final String orderByStr) {
        if ("ASC".equalsIgnoreCase(orderByStr.trim())) {
            return add(orderByColumn, OrderByType.ASC);
        } else if ("DESC".equalsIgnoreCase(orderByStr.trim())) {
            return add(orderByColumn, OrderByType.DESC);
        } else {
            Ansi.out().brightRed("排序类型有误 : " + orderByStr + " , 排序字段名称 : " + orderByColumn + " , 只能是 asc 或 desc (不区分大小写) !!!").println();
            return this;
        }
    }

    /**
     * 添加一个排序 SQL
     *
     * @param orderByColumn 排序 SQL ( SQL 表达式 )
     * @param orderByType   排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public OrderBy addSQL(final String orderByColumn, final OrderByType orderByType) {
        orderByList.add(new OrderByBody(orderByColumn, orderByType, true));
        return this;
    }

    /**
     * 添加一个排序 SQL
     *
     * @param orderByColumn 排序 SQL ( SQL 表达式 )
     * @param orderByStr    排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public OrderBy addSQL(final String orderByColumn, final String orderByStr) {
        if ("ASC".equalsIgnoreCase(orderByStr.trim())) {
            return addSQL(orderByColumn, OrderByType.ASC);
        } else if ("DESC".equalsIgnoreCase(orderByStr.trim())) {
            return addSQL(orderByColumn, OrderByType.DESC);
        } else {
            Ansi.out().brightRed("排序类型有误 : " + orderByStr + " , 排序字段名称 : " + orderByColumn + " , 只能是 asc 或 desc (不区分大小写) !!!").println();
            return this;
        }
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

    }

}
