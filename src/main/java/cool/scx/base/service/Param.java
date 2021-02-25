package cool.scx.base.service;

import cool.scx.enumeration.SortType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Param class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class Param<Entity> {
    private final Field[] entityFields;
    public Map<String, SortType> orderBy = new HashMap<>();//排序的字段
    public Set<String> groupBy = new HashSet<>();//自定义分组 SQL 添加
    public String whereSql = "";//自定义WHERE SQL添加
    public Entity queryObject;
    private Integer page = 0;//分页用
    private Integer limit = 0;//分页用

    /**
     * <p>Constructor for Param.</p>
     *
     * @param queryObject a Entity object.
     */
    public Param(Entity queryObject) {
        this.queryObject = queryObject;
        this.entityFields = queryObject.getClass().getFields();
    }

    /**
     * <p>addOrderBy.</p>
     *
     * @param orderByColumn a {@link java.lang.String} object.
     * @param sortType      a {@link cool.scx.enumeration.SortType} object.
     * @return a {@link Param} object.
     */
    public Param<Entity> addOrderBy(String orderByColumn, SortType sortType) {
        if (checkStringInFields(orderByColumn)) {
            this.orderBy.put(orderByColumn, sortType);
        } else {
            System.err.println(orderByColumn + " 不存在于 " + queryObject.getClass() + " 的 field 内 请检查 orderBy 字段是否正确 或直接采用 param.orderBy 进行赋值 !!!");
        }
        return this;
    }

    /**
     * <p>addGroupBy.</p>
     *
     * @param groupByColumn a {@link java.lang.String} object.
     * @return a {@link Param} object.
     */
    public Param<Entity> addGroupBy(String groupByColumn) {
        if (checkStringInFields(groupByColumn)) {
            this.groupBy.add(groupByColumn);
        } else {
            System.err.println(groupByColumn + " 不存在于 " + queryObject.getClass() + " 的 field 内 请检查 groupBy 字段是否正确 或直接采用 param.groupBy 进行赋值 !!!");
        }
        return this;
    }

    /**
     * <p>setPagination.</p>
     *
     * @param limit 每页数量
     * @param page  分页数量
     * @return p
     */
    public Param<Entity> setPagination(Integer page, Integer limit) {
        if (page >= 0 && limit >= 0) {
            this.page = page;
            this.limit = limit;
        } else {
            throw new RuntimeException("分页参数错误!!!");
        }
        return this;
    }

    /**
     * <p>setPagination.</p>
     *
     * @param limit a {@link java.lang.Integer} object.
     * @return a {@link Param} object.
     */
    public Param<Entity> setPagination(Integer limit) {
        if (limit >= 0) {
            this.page = 1;
            this.limit = limit;
        } else {
            throw new RuntimeException("分页参数错误!!!");
        }
        return this;
    }

    /**
     * <p>Getter for the field <code>page</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getPage() {
        return page;
    }

    /**
     * <p>Getter for the field <code>limit</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getLimit() {
        return limit;
    }

    private boolean checkStringInFields(String str) {
        for (var entityField : entityFields) {
            if (entityField.getName().equals(str)) {
                return true;
            }
        }
        return false;
    }
}
