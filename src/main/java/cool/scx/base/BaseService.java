package cool.scx.base;

import cool.scx.bo.*;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.dao.BaseDao;
import cool.scx.enumeration.OrderByType;
import cool.scx.enumeration.WhereType;
import cool.scx.sql.SQLBuilder;
import cool.scx.sql.SQLRunner;
import cool.scx.util.CaseUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 最基本的 service 类
 * <p>
 * 对 BaseDao 进行简单的封装以简化操作成本
 * <p>
 * 业务 service 可以继承此类 或手动创建 : new BaseService()
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class BaseService<Entity extends BaseModel> {

    /**
     * 实际用于操作数据的 baseDao
     */
    private final BaseDao<Entity> baseDao;

    /**
     * 实体类的 class , 用于一些基本操作
     */
    private final Class<Entity> entityClass;

    /**
     * 从泛型中获取 entityClass
     */
    @SuppressWarnings("unchecked")
    public BaseService() {
        this.entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.baseDao = new BaseDao<>(entityClass);
    }

    /**
     * 手动创建 entityClass
     *
     * @param entityClass 继承自 {@link cool.scx.base.BaseModel} 的实体类 class
     */
    public BaseService(Class<Entity> entityClass) {
        this.entityClass = entityClass;
        this.baseDao = new BaseDao<>(entityClass);
    }

    /**
     * 插入数据
     *
     * @param entity 待插入的数据
     * @return 插入后的数据
     */
    public Entity save(Entity entity) {
        var newId = this.baseDao.insert(entity);
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
            return this.baseDao.insertList(entityList);
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
            return this.baseDao.delete(new Where("id", WhereType.IN, ids));
        } else {// 逻辑删除
            var needTombstoneEntity = ScxContext.getBean(entityClass);
            needTombstoneEntity.tombstone = true;
            var where = new Where("id", WhereType.IN, ids).add("tombstone", WhereType.EQUAL, false);
            return this.baseDao.update(needTombstoneEntity, where, false).affectedLength;
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
            return this.baseDao.delete(where);
        } else {//逻辑删除
            var needTombstoneEntity = ScxContext.getBean(entityClass);
            needTombstoneEntity.tombstone = true;
            return this.baseDao.update(needTombstoneEntity, where, false).affectedLength;
        }
    }

    /**
     * 根据 ID 列表删除指定的数据  (注意 : 此方法会忽略配置文件强制使用物理删除)
     *
     * @param ids 要删除的数据的 id 集合
     * @return 删除成功的数据条数
     */
    public long deleteIgnoreConfig(long... ids) {
        return this.baseDao.delete(new Where("id", WhereType.IN, ids));
    }

    /**
     * 根据条件删除指定的数据  (注意 : 此方法会忽略配置文件强制使用物理删除)
     *
     * @param where 删除条件
     * @return 被删除的数据条数
     */
    public long deleteIgnoreConfig(Where where) {
        return this.baseDao.delete(where);
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
            return this.baseDao.update(needRevokeDeleteModel, where, false).affectedLength;
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
        return this.baseDao.update(entity, where, false).affectedLength;
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
        return this.baseDao.update(entity, where, true).affectedLength;
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
        var list = this.baseDao.select(where, null, null, new Pagination(1));
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据聚合查询条件 {@link cool.scx.bo.QueryParam} 获取单条数据
     *
     * @param queryParam 聚合查询参数对象
     * @return 查到多个则返回第一个 没有则返回 null
     */
    public Entity get(QueryParam queryParam) {
        if (!ScxConfig.realDelete()) {
            queryParam.addWhere("tombstone", WhereType.EQUAL, false);
        }
        queryParam.setPagination(1);
        var list = this.baseDao.select(queryParam.where, queryParam.groupBy, queryParam.orderBy, queryParam.pagination);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据聚合查询条件 {@link cool.scx.bo.QueryParam} 获取数据条数
     *
     * @param queryParam 聚合查询参数对象
     * @return 数据条数
     */
    public long count(QueryParam queryParam) {
        if (!ScxConfig.realDelete()) {
            queryParam.addWhere("tombstone", WhereType.EQUAL, false);
        }
        return this.baseDao.count(queryParam.where, queryParam.groupBy);
    }

    /**
     * 获取所有数据的条数
     *
     * @return 所有数据的条数
     */
    public long count() {
        var where = ScxConfig.realDelete() ? null : new Where("tombstone", WhereType.EQUAL, false);
        return this.baseDao.count(where, null);
    }

    /**
     * 根据聚合查询条件 {@link cool.scx.bo.QueryParam} 获取数据列表
     *
     * @param queryParam 聚合查询参数对象
     * @return 数据列表
     */
    public List<Entity> list(QueryParam queryParam) {
        if (!ScxConfig.realDelete()) {
            queryParam.addWhere("tombstone", WhereType.EQUAL, false);
        }
        return this.baseDao.select(queryParam.where, queryParam.groupBy, queryParam.orderBy, queryParam.pagination);
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
        return this.baseDao.select(where, null, null, null);
    }

    /**
     * 获取所有数据 (注意 : 默认根据最后更新时间 {@link cool.scx.base.BaseModel#updateDate} 排序)
     *
     * @return 所有数据
     */
    public List<Entity> list() {
        var where = ScxConfig.realDelete() ? null : new Where("tombstone", WhereType.EQUAL, false);
        var orderBy = new OrderBy("updateDate", OrderByType.DESC);
        return this.baseDao.select(where, null, orderBy, null);
    }

    /**
     * 根据 fieldName 获取 list 集合 一般做 Autocomplete 用
     *
     * @param fieldName 字段名称
     * @return 以 value 为键值的 list 集合
     */
    public List<Map<String, Object>> getFieldList(String fieldName) {
        //判断查询字段是否安全 ( 数据库字段内 防止 sql 注入)
        var isSafe = Arrays.stream(this.baseDao.tableInfo().allFields)
                .filter(field -> field.getName().equals(fieldName))
                .count() == 1;
        if (isSafe) {
            var selectColumn = CaseUtils.toSnake(fieldName) + " As value ";
            var where = ScxConfig.realDelete() ? null : new Where("tombstone", WhereType.EQUAL, false);
            var sqlBuilder = SQLBuilder.Select(this.baseDao.tableInfo().tableName)
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
     * 返回 对应的 baseDao 实例 方便进行一些细粒度的操作
     *
     * @return 当前 baseService 实例对应的 baseDao实例
     */
    public BaseDao<Entity> baseDao() {
        return this.baseDao;
    }

}
