package cool.scx.bo;

import cool.scx.enumeration.SortType;

/**
 * 查询参数类 方便传递参数使用<br>
 * 只是针对  GroupBy OrderBy Pagination Where 等进行的简单封装 <br>
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class Param<Entity> {

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
    public Param() {
    }

    /**
     * 添加排序项
     *
     * @param orderByColumn a {@link java.lang.String} object.
     * @param sortType      a {@link cool.scx.enumeration.SortType} object.
     * @return a 当前实例
     */
    public Param<Entity> addOrderBy(String orderByColumn, SortType sortType) {
        this.orderBy.add(orderByColumn, sortType);
        return this;
    }

    /**
     * 设置分组项
     *
     * @param groupByColumn a {@link java.lang.String} object.
     * @return a 当前实例
     */
    public Param<Entity> addGroupBy(String groupByColumn) {
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
    public Param<Entity> setPagination(Integer page, Integer limit) {
        pagination.set(page, limit);
        return this;
    }

    /**
     * 设置分页 默认 第一页
     *
     * @param limit a {@link java.lang.Integer} object.
     * @return a 当前实例
     */
    public Param<Entity> setPagination(Integer limit) {
        pagination.set(limit);
        return this;
    }

}
