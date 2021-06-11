package cool.scx.dao;

import cool.scx.annotation.Column;
import cool.scx.base.BaseModel;
import cool.scx.bo.Param;
import cool.scx.bo.TableInfo;
import cool.scx.bo.UpdateResult;
import cool.scx.sql.SQLBuilder;
import cool.scx.sql.SQLHelper;
import cool.scx.sql.SQLRunner;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;

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
     * <p>list.</p>
     *
     * @param param      参数对象.
     * @param ignoreLike a boolean.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> select(Param<Entity> param, boolean ignoreLike) {
        var sql = SQLBuilder.Select(table.tableName).SelectColumns(table.selectColumns)
                .Where(getWhereColumns(param.o, ignoreLike))
                .WhereSql(param.whereSql)
                .GroupBy(param.groupBy)
                .OrderBy(param.orderBy)
                .Pagination(param.getPage(), param.getLimit())
                .GetSQL();
        return SQLRunner.query(sql, ObjectUtils.beanToMap(param.o), entityClass);
    }

    /**
     * <p>count.</p>
     *
     * @param param      参数对象
     * @param ignoreLike a boolean.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer count(Param<Entity> param, boolean ignoreLike) {
        var sql = SQLBuilder.Select(table.tableName).SelectColumns(new String[]{"COUNT(*)"})
                .Where(getWhereColumns(param.o, ignoreLike))
                .WhereSql(param.whereSql)
                .GroupBy(param.groupBy)
                .GetSQL();
        return Integer.parseInt(SQLRunner.query(sql, ObjectUtils.beanToMap(param.o)).get(0).get("COUNT(*)").toString());
    }

    /**
     * <p>update.</p>
     *
     * @param param       参数对象
     * @param includeNull a boolean.
     * @return a {@link cool.scx.bo.UpdateResult} object.
     */
    public UpdateResult update(Param<Entity> param, boolean includeNull) {
        var entityMap = ObjectUtils.beanToMap(param.o);
        var setColumns = Stream.of(table.canUpdateFields)
                .filter(field -> (!includeNull && ObjectUtils.getFieldValue(field, param.o) != null))
                .toArray(Field[]::new);
        var sql = SQLBuilder.Update(table.tableName).UpdateColumns(setColumns);
        if (param.o.id != null) {
            sql.WhereSql(" id = :id ");
        } else if (!StringUtils.isEmpty(param.whereSql)) {
            sql.WhereSql(param.whereSql);
        } else {
            throw new RuntimeException("更新数据时必须指定 id 或 自定义的 where 语句 !!!");
        }
        return SQLRunner.update(sql.GetSQL(), entityMap);
    }

    /**
     * <p>delete.</p>
     *
     * @param param a 参数对象
     * @return a {@link java.lang.Integer} object.
     */
    public Integer delete(Param<Entity> param) {
        var entityMap = ObjectUtils.beanToMap(param.o);
        var sql = SQLBuilder.Delete(table.tableName);
        var whereColumns = getWhereColumns(param.o, false);
        if (whereColumns.length > 0 || StringUtils.isNotEmpty(param.whereSql)) {
            sql.Where(whereColumns).WhereSql(param.whereSql);
        } else {
            throw new RuntimeException("删除数据时必须指定 删除条件(queryObject) 或 自定义的 where 语句 !!!");
        }
        return SQLRunner.update(sql.GetSQL(), entityMap).affectedLength;
    }

    private String[] getWhereColumns(Entity entity, boolean ignoreLike) {
        if (ignoreLike) {
            return Stream.of(table.allFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null)
                    .map(field -> StringUtils.camelToUnderscore(field.getName()) + " = :" + field.getName()).toArray(String[]::new);
        } else {
            return Stream.of(table.allFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null)
                    .map(field -> {
                        var columnName = StringUtils.camelToUnderscore(field.getName());
                        var column = field.getAnnotation(Column.class);
                        if (column != null && column.useLike()) {
                            return columnName + " LIKE  CONCAT('%',:" + field.getName() + ",'%')";
                        } else {
                            return columnName + " = :" + field.getName();
                        }
                    }).toArray(String[]::new);
        }
    }

    public TableInfo table() {
        return table;
    }

}
