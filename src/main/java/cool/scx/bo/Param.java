package cool.scx.bo;

import cool.scx.enumeration.SortType;
import cool.scx.util.Ansi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 查询参数类
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class Param<Entity> {

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
    }

    /**
     * 添加排序项
     *
     * @param orderByColumn a {@link java.lang.String} object.
     * @param sortType      a {@link cool.scx.enumeration.SortType} object.
     * @return a 当前实例
     */
    public Param<Entity> addOrderBy(String orderByColumn, SortType sortType) {
        try {
            queryObject.getClass().getField(orderByColumn);
            this.orderBy.put(orderByColumn, sortType);
        } catch (NoSuchFieldException e) {
            Ansi.OUT.brightRed(orderByColumn + " 不存在于 " + queryObject.getClass() + " 的 field 内 请检查 orderBy 字段是否正确 或直接采用 param.orderBy 进行赋值 !!!").ln();
        }
        return this;
    }

    /**
     * 设置分组项
     *
     * @param groupByColumn a {@link java.lang.String} object.
     * @return a 当前实例
     */
    public Param<Entity> addGroupBy(String groupByColumn) {
        try {
            queryObject.getClass().getField(groupByColumn);
            this.groupBy.add(groupByColumn);
        } catch (NoSuchFieldException e) {
            Ansi.OUT.brightRed(groupByColumn + " 不存在于 " + queryObject.getClass() + " 的 field 内 请检查 groupBy 字段是否正确 或直接采用 param.groupBy 进行赋值 !!!").ln();
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
     * 设置分页 默认 第一页
     *
     * @param limit a {@link java.lang.Integer} object.
     * @return a 当前实例
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
     * 获取 分页索引
     *
     * @return a 分页索引
     */
    public Integer getPage() {
        return page;
    }

    /**
     * 获取分页大小
     *
     * @return a 分页大小
     */
    public Integer getLimit() {
        return limit;
    }

}
