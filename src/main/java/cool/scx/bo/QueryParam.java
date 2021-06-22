package cool.scx.bo;

import cool.scx.enumeration.OrderByType;
import cool.scx.enumeration.WhereType;

/**
 * 查询参数类<br>
 * 针对  GroupBy OrderBy Pagination Where 等进行的简单封装 <br>
 * 只是 为了方便传递参数使用<br>
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class QueryParam {

    /**
     * 排序的字段
     */
    public OrderBy orderBy = new OrderBy();

    /**
     * 自定义分组 SQL 添加
     */
    public GroupBy groupBy = new GroupBy();

    /**
     * 自定义WHERE 添加
     */
    public Where where = new Where();

    /**
     * 分页参数
     */
    public Pagination pagination = new Pagination();

    /**
     * <p>Constructor for Param.</p>
     */
    public QueryParam() {

    }


    public QueryParam addWhere(String fieldName, WhereType whereType, Object value1, Object value2) {
        this.where.add(fieldName, whereType, value1, value2);
        return this;
    }

    public QueryParam addWhere(String fieldName, WhereType whereType, Object value1) {
        this.where.add(fieldName, whereType, value1);
        return this;
    }

    /**
     * 设置分组项
     *
     * @param groupByColumn a {@link java.lang.String} object.
     * @return a 当前实例
     */
    public QueryParam addGroupBy(String groupByColumn) {
        this.groupBy.add(groupByColumn);
        return this;
    }

    /**
     * 设置分页参数
     *
     * @param page  分页页码
     * @param limit 每页数量
     * @return p
     */
    public QueryParam setPagination(Integer page, Integer limit) {
        pagination.set(page, limit);
        return this;
    }

    /**
     * 设置分页 默认 第一页
     *
     * @param limit a {@link java.lang.Integer} object.
     * @return a 当前实例
     */
    public QueryParam setPagination(Integer limit) {
        pagination.set(limit);
        return this;
    }

    /**
     * 添加排序项
     *
     * @param orderByColumn a {@link java.lang.String} object.
     * @param orderByType   a {@link cool.scx.enumeration.OrderByType} object.
     * @return a 当前实例
     */
    public QueryParam addOrderBy(String orderByColumn, OrderByType orderByType) {
        this.orderBy.add(orderByColumn, orderByType);
        return this;
    }

}
