package cool.scx.bo;

import cool.scx.enumeration.OrderByType;

import java.util.HashMap;
import java.util.Map;

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
    public final Map<String, OrderByType> orderByList = new HashMap<>();

    /**
     * <p>Constructor for OrderBy.</p>
     */
    public OrderBy() {

    }

    /**
     * <p>Constructor for OrderBy.</p>
     *
     * @param orderByColumn a {@link java.lang.String} object
     * @param orderByType   a {@link cool.scx.enumeration.OrderByType} object
     */
    public OrderBy(String orderByColumn, OrderByType orderByType) {
        add(orderByColumn, orderByType);
    }

    /**
     * 添加一个排序字段
     *
     * @param orderByColumn 排序字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @param orderByType   排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public OrderBy add(String orderByColumn, OrderByType orderByType) {
        orderByList.put(orderByColumn, orderByType);
        return this;
    }

    /**
     * 添加一个排序字段
     *
     * @param orderByColumn 排序字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @param orderByStr    排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public OrderBy add(String orderByColumn, String orderByStr) {
        if ("asc".equalsIgnoreCase(orderByStr.trim())) {
            return add(orderByColumn, OrderByType.ASC);
        } else {
            return add(orderByColumn, OrderByType.DESC);
        }
    }

}
