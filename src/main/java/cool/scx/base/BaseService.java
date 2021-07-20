package cool.scx.base;

import cool.scx.bo.*;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.OrderByType;
import cool.scx.enumeration.WhereType;
import cool.scx.sql.SQLBuilder;
import cool.scx.sql.SQLHelper;
import cool.scx.sql.SQLRunner;
import cool.scx.util.CaseUtils;
import cool.scx.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

/**
 * 最基本的 service 类 , 提供一些简单的 CRUD 操作
 * <p>
 * '_' 下划线开头的方法为具体实现方法 其余方法为实现方法的建议封装
 * <p>
 * 业务 service 可以继承此类 或手动创建 : new BaseService()
 * <p>
 * 如果 无法满足需求
 * <p>
 * 可以考虑使用 {@link cool.scx.sql.SQLRunner}
 *
 * @author scx567888
 * @version 0.3.6
 */
public class BaseService<Entity extends BaseModel> {

    /**
     * 实体类对应的 table 结构
     */
    private final TableInfo tableInfo;

    /**
     * 实体类 class 用于泛型转换
     */
    private final Class<Entity> entityClass;

    /**
     * 从泛型中获取 entityClass
     */
    @SuppressWarnings("unchecked")
    public BaseService() {
        this.entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.tableInfo = SQLHelper.getTableInfo(this.entityClass);
    }

    /**
     * 手动创建 entityClass
     *
     * @param entityClass 继承自 {@link cool.scx.base.BaseModel} 的实体类 class
     */
    public BaseService(Class<Entity> entityClass) {
        this.entityClass = entityClass;
        this.tableInfo = SQLHelper.getTableInfo(this.entityClass);
    }

    /**
     * 插入数据
     *
     * @param entity 待插入的数据
     * @return 插入后的数据
     */
    public Entity save(Entity entity) {
        var newId = this._insert(entity);
        return this.get(newId);
    }

    /**
     * 批量插入数据
     *
     * @param entityList 数据集合
     * @return 插入成功的数据的自增主键列表
     */
    public List<Long> save(List<Entity> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return new ArrayList<>();
        } else {
            return this._insertList(entityList);
        }
    }

    /**
     * 根据 ID 列表删除指定的数据
     *
     * @param ids 要删除的数据的 id 集合
     * @return 删除成功的数据条数
     */
    public long delete(long... ids) {
        //物理删除
        if (ScxConfig.realDelete()) {
            return this._delete(new Where("id", WhereType.IN, ids));
        } else {// 逻辑删除
            var needTombstoneEntity = ScxContext.getBean(entityClass);
            needTombstoneEntity.tombstone = true;
            var where = new Where("id", WhereType.IN, ids).add("tombstone", WhereType.EQUAL, false);
            return this._update(needTombstoneEntity, where, false);
        }
    }

    /**
     * 根据条件删除
     *
     * @param where 删除条件
     * @return 被删除的数据条数
     */
    public long delete(Where where) {
        //物理删除
        if (ScxConfig.realDelete()) {
            return this._delete(where);
        } else {//逻辑删除
            var needTombstoneEntity = ScxContext.getBean(entityClass);
            needTombstoneEntity.tombstone = true;
            return this._update(needTombstoneEntity, where, false);
        }
    }

    /**
     * 根据 ID 列表删除指定的数据  (注意 : 此方法会忽略配置文件强制使用物理删除)
     *
     * @param ids 要删除的数据的 id 集合
     * @return 删除成功的数据条数
     */
    public long deleteIgnoreConfig(long... ids) {
        return this._delete(new Where("id", WhereType.IN, ids));
    }

    /**
     * 根据条件删除指定的数据  (注意 : 此方法会忽略配置文件强制使用物理删除)
     *
     * @param where 删除条件
     * @return 被删除的数据条数
     */
    public long deleteIgnoreConfig(Where where) {
        return this._delete(where);
    }

    /**
     * 根据 ID 列表恢复删除的数据
     *
     * @param ids 待恢复的数据 id 集合
     * @return 恢复删除成功的数据条数
     */
    public long revokeDelete(long... ids) {
        return this.revokeDelete(new Where("id", WhereType.IN, ids));
    }

    /**
     * 根据指定条件恢复删除的数据
     *
     * @param where 指定的条件
     * @return 恢复删除成功的数据条数
     */
    public long revokeDelete(Where where) {
        if (ScxConfig.realDelete()) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            var needRevokeDeleteModel = ScxContext.getBean(entityClass);
            needRevokeDeleteModel.tombstone = false;
            return this._update(needRevokeDeleteModel, where, false);
        }
    }

    /**
     * 根据指定条件更新数据
     *
     * @param entity 待更新的数据
     * @param where  更新的条件
     * @return 更新成功的数据条数
     */
    public long update(Entity entity, Where where) {
        //逻辑删除时不更新 处于逻辑删除状态的数据
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        //更新成功的条数
        return this._update(entity, where, false);
    }

    /**
     * 根据  id 更新
     *
     * @param entity 待更新的数据 ( 注意: 请保证数据中 id 字段不为空 )
     * @return 更新成功后的数据
     */
    public Entity update(Entity entity) {
        if (entity.id == null) {
            throw new RuntimeException("根据 id 更新时 id 不能为空");
        }
        var l = this.update(entity, new Where("id", WhereType.EQUAL, entity.id));
        return l == 1 ? this.get(entity.id) : null;
    }

    /**
     * 根据指定条件更新数据 (注意 : 数据中的 null 值会被同步设置到数据库中)
     *
     * @param entity 待更新的数据
     * @param where  更新的条件
     * @return 更新成功的数据条数
     */
    public long updateIncludeNull(Entity entity, Where where) {
        //逻辑删除时不更新 处于逻辑删除状态的数据
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        //更新成功的条数
        return this._update(entity, where, true);
    }

    /**
     * 根据  id 更新  (注意 : 数据中的 null 值会被同步设置到数据库中)
     *
     * @param entity 待更新的数据 ( 注意: 请保证数据中 id 字段不为空 )
     * @return 更新成功后的数据
     */
    public Entity updateIncludeNull(Entity entity) {
        if (entity.id == null) {
            throw new RuntimeException("根据 id 更新时 id 不能为空");
        }
        var l = this.updateIncludeNull(entity, new Where("id", WhereType.EQUAL, entity.id));
        return l == 1 ? this.get(entity.id) : null;
    }

    /**
     * 根据 ID (主键) 查询单条数据
     *
     * @param id id ( 主键 )
     * @return 查到多个则返回第一个 没有则返回 null
     */
    public Entity get(long id) {
        var where = new Where("id", WhereType.EQUAL, id);
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        var list = this._select(where, null, null, new Pagination(1));
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据聚合查询条件 {@link cool.scx.bo.Query} 获取单条数据
     *
     * @param query 聚合查询参数对象
     * @return 查到多个则返回第一个 没有则返回 null
     */
    public Entity get(Query query) {
        if (!ScxConfig.realDelete()) {
            query.addWhere("tombstone", WhereType.EQUAL, false);
        }
        query.setPagination(1);
        var list = this._select(query.where(), query.groupBy(), query.orderBy(), query.pagination());
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据聚合查询条件 {@link cool.scx.bo.Query} 获取数据条数
     *
     * @param query 聚合查询参数对象
     * @return 数据条数
     */
    public long count(Query query) {
        if (!ScxConfig.realDelete()) {
            query.addWhere("tombstone", WhereType.EQUAL, false);
        }
        return this._count(query.where(), query.groupBy());
    }

    /**
     * 获取所有数据的条数
     *
     * @return 所有数据的条数
     */
    public long count() {
        var where = ScxConfig.realDelete() ? null : new Where("tombstone", WhereType.EQUAL, false);
        return this._count(where, null);
    }

    /**
     * 根据聚合查询条件 {@link cool.scx.bo.Query} 获取数据列表
     *
     * @param query 聚合查询参数对象
     * @return 数据列表
     */
    public List<Entity> list(Query query) {
        if (!ScxConfig.realDelete()) {
            query.addWhere("tombstone", WhereType.EQUAL, false);
        }
        return this._select(query.where(), query.groupBy(), query.orderBy(), query.pagination());
    }

    /**
     * 根据 ID (主键) 列表 获取数据列表
     *
     * @param ids ID (主键) 列表
     * @return 数据列表
     */
    public List<Entity> list(long... ids) {
        var where = new Where("id", WhereType.IN, ids);
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        return this._select(where, null, null, null);
    }

    /**
     * 获取所有数据 (注意 : 默认根据最后更新时间 {@link cool.scx.base.BaseModel#updateDate} 排序)
     *
     * @return 所有数据
     */
    public List<Entity> list() {
        var where = ScxConfig.realDelete() ? null : new Where("tombstone", WhereType.EQUAL, false);
        var orderBy = new OrderBy("updateDate", OrderByType.DESC);
        return this._select(where, null, orderBy, null);
    }

    /**
     * 根据 fieldName 获取 list 集合 一般做 Autocomplete 用
     *
     * @param fieldName 字段名称
     * @return 以 value 为键值的 list 集合
     */
    public List<Map<String, Object>> getFieldList(String fieldName) {
        //判断查询字段是否安全 ( 数据库字段内 防止 sql 注入)
        var isSafe = Arrays.stream(this.tableInfo.allFields)
                .filter(field -> field.getName().equals(fieldName))
                .count() == 1;
        if (isSafe) {
            var selectColumn = CaseUtils.toSnake(fieldName) + " As value ";
            var where = ScxConfig.realDelete() ? null : new Where("tombstone", WhereType.EQUAL, false);
            var sqlBuilder = SQLBuilder.Select(this.tableInfo.tableName)
                    .SelectColumns(selectColumn)
                    .Where(where)
                    .GroupBy(new GroupBy("value"));
            var whereParamMap = sqlBuilder.GetWhereParamMap();
            var sql = sqlBuilder.GetSQL();
            return SQLRunner.query(sql, whereParamMap);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 保存单条数据
     *
     * @param entity 待插入的数据
     * @return 插入成功的主键 ID 如果插入失败则返回 null
     */
    public Long _insert(Entity entity) {
        var c = Stream.of(tableInfo.canInsertFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null).toArray(Field[]::new);
        var sql = SQLBuilder.Insert(tableInfo.fullTableName).InsertColumns(c).Values(c).GetSQL();
        var updateResult = SQLRunner.update(sql, ObjectUtils.beanToMap(entity));
        return updateResult.generatedKeys.size() > 0 ? updateResult.generatedKeys.get(0) : -1;
    }

    /**
     * 保存多条数据
     *
     * @param entityList 待保存的列表
     * @return 保存成功的主键 (ID) 列表
     */
    public List<Long> _insertList(List<Entity> entityList) {
        //获取 sql 语句
        var sql = SQLBuilder.Insert(tableInfo.fullTableName).InsertColumns(tableInfo.canInsertFields)
                .Values(tableInfo.canInsertFields).GetSQL();
        //将 entity 转换为 map
        var mapList = new ArrayList<Map<String, Object>>(entityList.size());
        for (var entity : entityList) {
            var map = new HashMap<String, Object>();
            for (var canInsertField : tableInfo.canInsertFields) {
                map.put(canInsertField.getName(), ObjectUtils.getFieldValue(canInsertField, entity));
            }
            mapList.add(map);
        }
        return SQLRunner.updateBatch(sql, mapList).generatedKeys;
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
    public List<Entity> _select(Where where, GroupBy groupBy, OrderBy orderBy, Pagination pagination) {
        var sqlBuilder = SQLBuilder.Select(tableInfo.fullTableName).SelectColumns(tableInfo.selectColumns)
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
    public long _count(Where where, GroupBy groupBy) {
        var sqlBuilder = SQLBuilder.Select(tableInfo.fullTableName).SelectColumns("COUNT(*) AS count")
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
    public long _update(Entity entity, Where where, boolean includeNull) {
        if (where == null || where.isEmpty()) {
            throw new RuntimeException("更新数据时 必须指定 id , 删除条件 或 自定义的 where 语句 !!!");
        }
        var u = includeNull ? tableInfo.canUpdateFields : Stream.of(tableInfo.canUpdateFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null).toArray(Field[]::new);
        if (u.length == 0) {
            throw new RuntimeException("更新数据时 待更新的数据 [实体类中除被 @Column(excludeOnUpdate = true) 修饰以外的字段] 不能全部为 null !!!");
        }
        var sqlBuilder = SQLBuilder.Update(tableInfo.fullTableName).UpdateColumns(u).Where(where);
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
    public long _delete(Where where) {
        if (where == null || where.isEmpty()) {
            throw new RuntimeException("更新数据时必须指定 id,删除条件 或 自定义的 where 语句 !!!");
        }
        var sqlBuilder = SQLBuilder.Delete(tableInfo.fullTableName).Where(where);
        var whereParamMap = sqlBuilder.GetWhereParamMap();
        var sql = sqlBuilder.GetSQL();
        return SQLRunner.update(sql, whereParamMap).affectedLength;
    }

}
