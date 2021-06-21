package cool.scx.dao;

import cool.scx.base.BaseModel;
import cool.scx.bo.*;
import cool.scx.sql.SQLBuilder;
import cool.scx.sql.SQLHelper;
import cool.scx.sql.SQLRunner;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * 基本 Dao
 * <p>
 * 提供一些简单的数据库操作
 * <p>
 * 如果BaseDao 无法满足需求
 * <p>
 * 可以考虑使用 {@link cool.scx.sql.SQLRunner}
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class BaseDao<Entity extends BaseModel> {

    /**
     * 多条插入时 分割的大小
     */
    private static final int splitSize = 5000;

    /**
     * 实体类对应的 table 结构
     */
    private final TableInfo table;

    /**
     * 实体类 class 用于泛型转换
     */
    private final Class<Entity> entityClass;

    /**
     * <p>Constructor for BaseDao.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     */
    public BaseDao(Class<Entity> clazz) {
        table = SQLHelper.getTableInfo(clazz);
        entityClass = clazz;
    }

    /**
     * 保存单条数据
     *
     * @param entity a Entity object.
     * @return a {@link java.lang.Long} object.
     */
    public Long insert(Entity entity) {
        var c = Stream.of(table.canInsertFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null).toArray(Field[]::new);
        var sql = SQLBuilder.Insert(table.tableName).Columns(c).Values(c).GetSQL();
        var updateResult = SQLRunner.update(sql, ObjectUtils.beanToMap(entity));
        return updateResult.generatedKeys.size() > 0 ? updateResult.generatedKeys.get(0) : -1;
    }

    /**
     * 保存多条数据
     *
     * @param entityList a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public List<Long> insertList(List<Entity> entityList) {
        var size = entityList.size();
        if (size > splitSize) {
            Ansi.OUT.brightRed("批量插入数据量过大 , 达到" + size + "条 !!! 已按照" + splitSize + "条进行切分 !!!").ln();
            var generatedKeys = new ArrayList<Long>(splitSize);
            double number = Math.ceil(1.0 * size / splitSize);
            for (int i = 0; i < number; i++) {
                generatedKeys.addAll(insertList(entityList.subList(i * splitSize, (Math.min((i + 1) * splitSize, size)))));
            }
            return generatedKeys;
        }
        var values = new String[entityList.size()][table.canInsertFields.length];
        var map = new LinkedHashMap<String, Object>();

        for (int i = 0; i < entityList.size(); i++) {
            for (int j = 0; j < table.canInsertFields.length; j++) {
                values[i][j] = ":list" + i + "." + table.canInsertFields[j].getName();
            }
            //将 list 集合降级为 一维 map 结构 key 为  list{index}.{field} index 为索引 field 为字段名称
            map.putAll(ObjectUtils.beanToMapWithIndex(i, entityList.get(i)));
        }

        var sql = SQLBuilder.Insert(table.tableName).Columns(table.canInsertFields).Values(values).GetSQL();

        return SQLRunner.update(sql, map).generatedKeys;
    }

    /**
     * 获取列表
     *
     * @param where      查询过滤条件.
     * @param groupBy    a 分组条件.
     * @param orderBy    a 排序条件.
     * @param pagination 分页条件
     * @return a {@link java.util.List} object.
     */
    public List<Entity> select(Where where, GroupBy groupBy, OrderBy orderBy, Pagination pagination) {
        var sqlBuilder = SQLBuilder.Select(table.tableName).SelectColumns(table.selectColumns)
                .Where(where)
                .GroupBy(groupBy)
                .OrderBy(orderBy)
                .Pagination(pagination);

        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();

        return SQLRunner.query(sql, whereParamMap, entityClass);
    }

    /**
     * 获取条数
     *
     * @param where   查询条件
     * @param groupBy 分组条件
     * @return a {@link java.lang.Integer} object.
     */
    public Integer count(Where where, GroupBy groupBy) {
        var sqlBuilder = SQLBuilder.Select(table.tableName)
                .SelectColumns(new String[]{"COUNT(*)"})
                .Where(where)
                .GroupBy(groupBy);
        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();
        return Integer.parseInt(SQLRunner.query(sql, whereParamMap).get(0).get("COUNT(*)").toString());
    }

    /**
     * 更新数据
     *
     * @param entity      要更新的数据
     * @param where       更新的过滤条件
     * @param includeNull a boolean.
     * @return a {@link cool.scx.bo.UpdateResult} object.
     */
    public UpdateResult update(Entity entity, Where where, boolean includeNull) {
        if (where.isEmpty()) {
            throw new RuntimeException("更新数据时必须指定 id,删除条件 或 自定义的 where 语句 !!!");
        }
        var entityMap = ObjectUtils.beanToMap(entity);
        var setColumns = Stream.of(table.canUpdateFields)
                .filter(field -> (!includeNull && ObjectUtils.getFieldValue(field, entity) != null))
                .toArray(Field[]::new);
        var sqlBuilder = SQLBuilder.Update(table.tableName).UpdateColumns(setColumns).Where(where);

        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();
        //合并两个 map 包括更新数据的 map 和 where 条件的 map
        entityMap.putAll(whereParamMap);
        return SQLRunner.update(sql, entityMap);
    }

    /**
     * 删除数据
     *
     * @param where where 条件
     * @return a {@link java.lang.Integer} object.
     */
    public Integer delete(Where where) {
        if (where.isEmpty()) {
            throw new RuntimeException("更新数据时必须指定 id,删除条件 或 自定义的 where 语句 !!!");
        }
        var sqlBuilder = SQLBuilder.Delete(table.tableName).Where(where);
        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();
        return SQLRunner.update(sql, whereParamMap).affectedLength;
    }

    /**
     * <p>table.</p>
     *
     * @return a {@link cool.scx.bo.TableInfo} object
     */
    public TableInfo table() {
        return table;
    }

}
