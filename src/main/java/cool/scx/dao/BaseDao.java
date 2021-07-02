package cool.scx.dao;

import cool.scx.base.BaseModel;
import cool.scx.bo.*;
import cool.scx.sql.SQLBuilder;
import cool.scx.sql.SQLHelper;
import cool.scx.sql.SQLRunner;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;
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
 * @author scx567888
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
    private final TableInfo tableInfo;

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
        tableInfo = SQLHelper.getTableInfo(clazz);
        entityClass = clazz;
    }

    /**
     * 将实体类转为 map 并添加索引
     * 注意 此方法只能转换第一层
     *
     * @param index a {@link java.lang.Integer} object.
     * @param o     a {@link java.lang.Object} object.
     * @return a {@link java.util.Map} object.
     */
    private static Map<String, Object> beanToMapWithIndex(Integer index, Object o) {
        var clazzFields = o.getClass().getFields(); // 获取所有方法
        var objectMap = new HashMap<String, Object>(1 + (int) (clazzFields.length / 0.75));
        for (var field : clazzFields) {
            objectMap.put("list" + index + "." + field.getName(), ObjectUtils.getFieldValue(field, o));
        }
        return objectMap;
    }

    /**
     * 保存单条数据
     *
     * @param entity 待插入的数据
     * @return 插入成功的主键 ID 如果插入失败则返回 null
     */
    public Long insert(Entity entity) {
        var c = Stream.of(tableInfo.canInsertFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null).toArray(Field[]::new);
        var sql = SQLBuilder.Insert(tableInfo.tableName).InsertColumns(c).Values(c).GetSQL();
        var updateResult = SQLRunner.update(sql, ObjectUtils.beanToMap(entity));
        return updateResult.generatedKeys.size() > 0 ? updateResult.generatedKeys.get(0) : -1;
    }

    /**
     * 保存多条数据
     *
     * @param entityList 待保存的列表
     * @return 保存成功的主键 (ID) 列表
     */
    public List<Long> insertList(List<Entity> entityList) {
        var size = entityList.size();
        if (size > splitSize) {
            Ansi.OUT.brightRed("批量插入数据量过大 , 达到" + size + "条 !!! 已按照 " + splitSize + " 条一组进行切割并分段插入 !!!").ln();
            var generatedKeys = new ArrayList<Long>(splitSize);
            double number = Math.ceil(1.0 * size / splitSize);
            for (int i = 0; i < number; i++) {
                generatedKeys.addAll(insertList(entityList.subList(i * splitSize, (Math.min((i + 1) * splitSize, size)))));
            }
            return generatedKeys;
        }
        var values = new String[entityList.size()][tableInfo.canInsertFields.length];
        var map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < entityList.size(); i++) {
            for (int j = 0; j < tableInfo.canInsertFields.length; j++) {
                values[i][j] = ":list" + i + "." + tableInfo.canInsertFields[j].getName();
            }
            //将 list 集合降级为 一维 map 结构 key 为  list{index}.{field} index 为索引 field 为字段名称
            map.putAll(beanToMapWithIndex(i, entityList.get(i)));
        }
        var sql = SQLBuilder.Insert(tableInfo.tableName).InsertColumns(tableInfo.canInsertFields)
                .Values(values).GetSQL();
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
        var sqlBuilder = SQLBuilder.Select(tableInfo.tableName).SelectColumns(tableInfo.selectColumns)
                .Where(where).GroupBy(groupBy).OrderBy(orderBy).Pagination(pagination);
        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();
        return SQLRunner.query(sql, whereParamMap, entityClass);
    }

    /**
     * 获取条数
     *
     * @param where   查询条件
     * @param groupBy 分组条件
     * @return 条数
     */
    public long count(Where where, GroupBy groupBy) {
        var sqlBuilder = SQLBuilder.Select(tableInfo.tableName).SelectColumns("COUNT(*) AS count")
                .Where(where).GroupBy(groupBy);
        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();
        return (Long) SQLRunner.query(sql, whereParamMap).get(0).get("count");
    }

    /**
     * 更新数据
     *
     * @param entity      要更新的数据
     * @param where       更新的过滤条件
     * @param includeNull a boolean.
     * @return 受影响的条数
     */
    public long update(Entity entity, Where where, boolean includeNull) {
        if (where == null || where.isEmpty()) {
            throw new RuntimeException("更新数据时 必须指定 id , 删除条件 或 自定义的 where 语句 !!!");
        }
        var u = includeNull ? tableInfo.canUpdateFields : Stream.of(tableInfo.canUpdateFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null).toArray(Field[]::new);
        if (u.length == 0) {
            throw new RuntimeException("更新数据时 待更新的数据 [实体类中除被 @Column(excludeOnUpdate = true) 修饰以外的字段] 不能全部为 null !!!");
        }
        var sqlBuilder = SQLBuilder.Update(tableInfo.tableName).UpdateColumns(u).Where(where);
        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var entityMap = ObjectUtils.beanToMap(entity);
        entityMap.putAll(whereParamMap);
        var sql = sqlBuilder.GetSQL();
        return SQLRunner.update(sql, entityMap).affectedLength;
    }

    /**
     * 删除数据
     *
     * @param where where 条件
     * @return 受影响的条数
     */
    public long delete(Where where) {
        if (where == null || where.isEmpty()) {
            throw new RuntimeException("更新数据时必须指定 id,删除条件 或 自定义的 where 语句 !!!");
        }
        var sqlBuilder = SQLBuilder.Delete(tableInfo.tableName).Where(where);
        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();
        return SQLRunner.update(sql, whereParamMap).affectedLength;
    }

    /**
     * 获取 tableInfo 方便细粒度操作
     *
     * @return 当前 baseDao 的 tableInfo
     */
    public TableInfo tableInfo() {
        return tableInfo;
    }

}
