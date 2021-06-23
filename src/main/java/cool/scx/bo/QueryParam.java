package cool.scx.bo;

import cool.scx.base.BaseModel;
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
    public final OrderBy orderBy = new OrderBy();

    /**
     * 自定义分组 SQL 添加
     */
    public final GroupBy groupBy = new GroupBy();

    /**
     * 自定义WHERE 添加
     */
    public final Where where = new Where();

    /**
     * 分页参数
     */
    public final Pagination pagination = new Pagination();

    /**
     * <p>Constructor for Param.</p>
     */
    public QueryParam() {

    }


    /**
     * <p>addWhere.</p>
     *
     * @param fieldName a {@link java.lang.String} object
     * @param whereType a {@link cool.scx.enumeration.WhereType} object
     * @param value1    a {@link java.lang.Object} object
     * @param value2    a {@link java.lang.Object} object
     * @return a {@link cool.scx.bo.QueryParam} object
     */
    public QueryParam addWhere(String fieldName, WhereType whereType, Object value1, Object value2) {
        this.where.add(fieldName, whereType, value1, value2);
        return this;
    }

    /**
     * <p>setWhereSQL.</p>
     *
     * @param whereSQL a {@link java.lang.String} object
     * @return a {@link cool.scx.bo.QueryParam} object
     */
    public QueryParam setWhereSQL(String whereSQL) {
        this.where.whereSQL(whereSQL);
        return this;
    }

    /**
     * <p>addWhereByObject.</p>
     *
     * @param entity   a Entity object
     * @param <Entity> a Entity class
     * @return a {@link cool.scx.bo.QueryParam} object
     */
    public <Entity extends BaseModel> QueryParam addWhereByObject(Entity entity) {
        this.where.addByObject(entity);
        return this;
    }

    /**
     * 添加一个查询条件 (注意 : 此处添加的所有条件都会以 and 拼接 , 如需使用 or 请考虑使用 {@link cool.scx.bo.Where#whereSQL(String)} })
     *
     * @param fieldName 字段名称 (注意不是数据库名称)
     * @param whereType where 类型
     * @param value1    参数1
     * @return 本身 , 方便链式调用
     */
    public QueryParam addWhere(String fieldName, WhereType whereType, Object value1) {
        this.where.add(fieldName, whereType, value1);
        return this;
    }

    /**
     * <p>addWhere.</p>
     *
     * @param fieldName a {@link java.lang.String} object
     * @param whereType a {@link cool.scx.enumeration.WhereType} object
     * @return a {@link cool.scx.bo.QueryParam} object
     */
    public QueryParam addWhere(String fieldName, WhereType whereType) {
        this.where.add(fieldName, whereType);
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

    /**
     * <p>addOrderBy.</p>
     *
     * @param orderByColumn a {@link java.lang.String} object
     * @param str           a {@link java.lang.String} object
     * @return a {@link cool.scx.bo.QueryParam} object
     */
    public QueryParam addOrderBy(String orderByColumn, String str) {
        this.orderBy.add(orderByColumn, str);
        return this;
    }

}
