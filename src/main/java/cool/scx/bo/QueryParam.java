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
    private final OrderBy orderBy = new OrderBy();

    /**
     * 自定义分组 SQL 添加
     */
    private final GroupBy groupBy = new GroupBy();

    /**
     * 自定义WHERE 添加
     */
    private final Where where = new Where();

    /**
     * 分页参数
     */
    private final Pagination pagination = new Pagination();

    /**
     * <p>Constructor for Param.</p>
     */
    public QueryParam() {

    }

    /**
     * 添加一个查询条件 (注意 : 此处添加的所有条件都会以 and 拼接 , 如需使用 or 请考虑使用 {@link Where#whereSQL(String)} })
     *
     * @param fieldName 字段名称 (注意不是数据库名称)
     * @param whereType where 类型
     * @param value1    参数1
     * @param value2    参数2
     * @return 本身 , 方便链式调用
     */
    public QueryParam addWhere(String fieldName, WhereType whereType, Object value1, Object value2) {
        this.where.add(fieldName, whereType, value1, value2);
        return this;
    }

    /**
     * 设置 whereSql 适用于 复杂查询的自定义 where 子句<br>
     * 在最终 sql 中会拼接到 where 子句的最后<br>
     * 注意 :  除特殊语法外不需要手动在头部添加 AND
     *
     * @param whereSQL sql 语句
     * @return 本身 , 方便链式调用
     */
    public QueryParam setWhereSQL(String whereSQL) {
        this.where.whereSQL(whereSQL);
        return this;
    }

    /**
     * 直接根据实体类生成 Where 条件 (注意此处的所有 whereType 都是 EQUAL 或 LIKE (使用首尾全匹配) ) <br>
     * 如需更细粒度的控制请使用 add 方法
     *
     * @param entity   e
     * @param <Entity> e
     * @return 返回自己 方便链式调用
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
     * 添加一个查询条件 (注意 : 此处添加的所有条件都会以 and 拼接 , 如需使用 or 请考虑使用 {@link Where#whereSQL(String)} })
     *
     * @param fieldName 字段名称 (注意不是数据库名称)
     * @param whereType where 类型
     * @return 本身 , 方便链式调用
     */
    public QueryParam addWhere(String fieldName, WhereType whereType) {
        this.where.add(fieldName, whereType);
        return this;
    }

    /**
     * 添加一个 分组字段
     *
     * @param fieldName 分组字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @return 本身, 方便链式调用
     */
    public QueryParam addGroupBy(String fieldName) {
        this.groupBy.add(fieldName);
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
     * 添加一个排序字段
     *
     * @param orderByColumn 排序字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @param orderByType   排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public QueryParam addOrderBy(String orderByColumn, OrderByType orderByType) {
        this.orderBy.add(orderByColumn, orderByType);
        return this;
    }

    /**
     * 添加一个排序字段
     *
     * @param orderByColumn 排序字段的名称 (注意是实体类的字段名 , 不是数据库中的字段名)
     * @param orderByStr    排序类型 正序或倒序
     * @return 本身, 方便链式调用
     */
    public QueryParam addOrderBy(String orderByColumn, String orderByStr) {
        this.orderBy.add(orderByColumn, orderByStr);
        return this;
    }

    /**
     * 不在其中
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam notIn(String fieldName, Object value) {
        this.where.notIn(fieldName, value);
        return this;
    }

    /**
     * 在其中
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam in(String fieldName, Object value) {
        this.where.in(fieldName, value);
        return this;
    }

    /**
     * 包含  : 一般用于 JSON 格式字段 区别于 in
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam jsonContains(String fieldName, Object value) {
        this.where.jsonContains(fieldName, value);
        return this;
    }


    /**
     * not like : 默认会在首尾添加 %
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     默认会在首尾添加 %
     * @return this 方便链式调用
     */
    public QueryParam notLike(String fieldName, Object value) {
        this.where.notLike(fieldName, value);
        return this;
    }

    /**
     * like : 默认会在首尾添加 %
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     参数 默认会在首尾添加 %
     * @return this 方便链式调用
     */
    public QueryParam like(String fieldName, Object value) {
        this.where.like(fieldName, value);
        return this;
    }


    /**
     * not like : 根据 SQL 表达式进行判断
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     SQL 表达式
     * @return this 方便链式调用
     */
    public QueryParam notLikeRegex(String fieldName, String value) {
        this.where.notLikeRegex(fieldName, value);
        return this;
    }


    /**
     * like : 根据 SQL 表达式进行判断
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     SQL 表达式
     * @return this 方便链式调用
     */
    public QueryParam likeRegex(String fieldName, String value) {
        this.where.likeRegex(fieldName, value);
        return this;
    }

    /**
     * 不处于两者之间
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value1    比较值1
     * @param value2    比较值2
     * @return this 方便链式调用
     */
    public QueryParam notBetween(String fieldName, Object value1, Object value2) {
        this.where.notBetween(fieldName, value1, value2);
        return this;
    }


    /**
     * 两者之间
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value1    比较值1
     * @param value2    比较值2
     * @return this 方便链式调用
     */
    public QueryParam between(String fieldName, Object value1, Object value2) {
        this.where.between(fieldName, value1, value2);
        return this;
    }

    /**
     * 小于等于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam lessThanOrEqual(String fieldName, Object value) {
        this.where.lessThanOrEqual(fieldName, value);
        return this;
    }

    /**
     * 小于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam lessThan(String fieldName, Object value) {
        this.where.lessThan(fieldName, value);
        return this;
    }

    /**
     * 大于等于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam greaterThanOrEqual(String fieldName, Object value) {
        this.where.greaterThanOrEqual(fieldName, value);
        return this;
    }

    /**
     * 大于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam greaterThan(String fieldName, Object value) {
        this.where.greaterThan(fieldName, value);
        return this;
    }

    /**
     * 不相等
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam notEqual(String fieldName, Object value) {
        this.where.notEqual(fieldName, value);
        return this;
    }

    /**
     * 相等
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public QueryParam equal(String fieldName, Object value) {
        this.where.equal(fieldName, value);
        return this;
    }

    /**
     * 不为空
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @return this 方便链式调用
     */
    public QueryParam isNotNull(String fieldName) {
        this.where.isNotNull(fieldName);
        return this;
    }

    /**
     * 为空
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @return this 方便链式调用
     */
    public QueryParam isNull(String fieldName) {
        this.where.isNull(fieldName);
        return this;
    }

    public OrderBy orderBy() {
        return orderBy;
    }

    public GroupBy groupBy() {
        return groupBy;
    }

    public Where where() {
        return where;
    }

    public Pagination pagination() {
        return pagination;
    }

}
