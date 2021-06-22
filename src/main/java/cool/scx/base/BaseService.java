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
import java.util.*;

/**
 * 最基本的 service 类
 * <p>
 * 对 BaseDao 进行了封装以简化操作成本
 * <p>
 * 业务 service 需继承此类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public abstract class BaseService<Entity extends BaseModel> {

    /**
     * 实际用于操作数据的  baseDao
     */
    private final BaseDao<Entity> baseDao;

    /**
     * 实体类的 class 用于一些基本操作
     */
    private final Class<Entity> entityClass;

    /**
     * Constructor for BaseService
     */
    @SuppressWarnings("unchecked")
    public BaseService() {
        entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        baseDao = new BaseDao<>(entityClass);
    }

    /**
     * 实体插入新对象,并返回主键id值
     *
     * @param entity 实体
     * @return 实体
     */
    public Entity save(Entity entity) {
        var newId = baseDao.insert(entity);
        return getById(newId);
    }

    /**
     * 批量保存实体
     *
     * @param entityList 实体集合
     * @return 插入成功的数据 自增主键
     */
    public List<Long> saveList(List<Entity> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return new ArrayList<>();
        } else {
            return baseDao.insertList(entityList);
        }
    }

    /**
     * 删除指定id的实体
     *
     * @param ids 要删除的 id 集合
     * @return 被删除的数据条数 用于前台分页优化
     */
    public Integer deleteByIds(Long... ids) {
        //分逻辑删除和物理删除
        if (ScxConfig.realDelete()) {
            var where = new Where().add("id", WhereType.IN, ids);
            return baseDao.delete(where);
        } else {
            var needUpdateModel = ScxContext.getBean(entityClass);
            needUpdateModel.tombstone = true;
            var where = new Where().add("id", WhereType.IN, ids)
                    .add("tombstone", WhereType.EQUAL, false);
            return baseDao.update(needUpdateModel, where, false).affectedLength;
        }
    }

    /**
     * 根据条件删除
     *
     * @param where e
     * @return e
     */
    public Integer delete(Where where) {
        if (ScxConfig.realDelete()) {
            return baseDao.delete(where);
        } else {
            var needUpdateModel = ScxContext.getBean(entityClass);
            needUpdateModel.tombstone = true;
            return baseDao.update(needUpdateModel, where, false).affectedLength;
        }
    }

    /**
     * 批量删除
     *
     * @param whereList a {@link java.util.List} object.
     * @return 一共删除的数量
     */
    public Integer deleteList(Where... whereList) {
        var deleteCount = 0;
        for (Where where : whereList) {
            deleteCount += delete(where);
        }
        return deleteCount;
    }

    /**
     * 删除指定id的实体
     *
     * @param ids 要删除的 id 集合
     * @return 被删除的数据条数 用于前台分页优化
     */
    public Integer revokeDeleteByIds(Long... ids) {
        if (ScxConfig.realDelete()) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            var needRevokeDeleteModel = ScxContext.getBean(entityClass);
            needRevokeDeleteModel.tombstone = false;
            Where where = new Where().add("id", WhereType.IN, ids);
            return baseDao.update(needRevokeDeleteModel, where, false).affectedLength;
        }
    }

    /**
     * 根据条件恢复删除
     *
     * @param where e
     * @return e
     */
    public Integer revokeDelete(Where where) {
        if (ScxConfig.realDelete()) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            var needRevokeDeleteModel = ScxContext.getBean(entityClass);
            needRevokeDeleteModel.tombstone = false;
            return baseDao.update(needRevokeDeleteModel, where, false).affectedLength;
        }
    }

    /**
     * 批量恢复数据
     *
     * @param whereList a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer revokeDeleteList(Where... whereList) {
        var deleteCount = 0;
        if (ScxConfig.realDelete()) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            for (Where where : whereList) {
                deleteCount += revokeDelete(where);
            }
        }
        return deleteCount;
    }

    /**
     * 删除指定id的实体
     *
     * @param ids 要删除的 id 集合
     * @return 被删除的数据条数 用于前台分页优化
     */
    public Integer deleteByIdsIgnoreConfig(Long... ids) {
        Where where = new Where().add("id", WhereType.IN, ids);
        return baseDao.delete(where);
    }

    /**
     * 根据条件删除
     *
     * @param where e
     * @return e
     */
    public Integer deleteIgnoreConfig(Where where) {
        return baseDao.delete(where);
    }

    /**
     * 批量删除 强制使用 物理删除
     *
     * @param whereList a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer deleteListIgnoreConfig(Where... whereList) {
        var deleteCount = 0;
        for (Where where : whereList) {
            deleteCount += deleteIgnoreConfig(where);
        }
        return deleteCount;
    }

    /**
     * <p>update.</p>
     *
     * @param entity a {@link cool.scx.bo.QueryParam} object.
     * @param where  a {@link cool.scx.bo.QueryParam} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> update(Entity entity, Where where) {
        entity.tombstone = ScxConfig.realDelete() ? null : false;
        var ids = baseDao.update(entity, where, false);
        //此处重新查询一遍是为了保证数据的一致性
        Where selectWhere = new Where();
        if (!ScxConfig.realDelete()) {
            selectWhere.add("tombstone", WhereType.EQUAL, false);
        }
        return baseDao.select(selectWhere, null, null, null);
    }

    /**
     * 根据 id 更新
     *
     * @param entity a Entity object.
     * @return a Entity object.
     */
    public Entity update(Entity entity) {
        var id = entity.id;
        if (id == null) {
            throw new RuntimeException("根据 id 更新时 id 不能为空");
        }
        var where = new Where().add("id", WhereType.EQUAL, id);
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        var ids = baseDao.update(entity, where, false);
        var list = baseDao.select(
                new Where().add("id", WhereType.EQUAL, id),
                null,
                null,
                new Pagination().set(1));
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据 where 更新 包含 null
     *
     * @param where  更新条件
     * @param entity a Entity object
     * @return 更新后的数据
     */
    public List<Entity> updateIncludeNull(Entity entity, Where where) {
        entity.tombstone = ScxConfig.realDelete() ? null : false;
        var ids = baseDao.update(entity, where, true);
        //此处重新查询一遍是为了保证数据的一致性
        Where selectWhere = new Where();
        if (!ScxConfig.realDelete()) {
            selectWhere.add("tombstone", WhereType.EQUAL, false);
        }
        return baseDao.select(selectWhere, null, null, null);
    }

    /**
     * 根据 id 更新 同时包含 null 值
     *
     * @param entity a Entity object.
     * @return a Entity object.
     */
    public Entity updateIncludeNull(Entity entity) {
        var id = entity.id;
        if (id == null) {
            throw new RuntimeException("根据 id 更新时 id 不能为空");
        }
        var where = new Where().add("id", WhereType.EQUAL, id);
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        var ids = baseDao.update(entity, where, true);
        var list = baseDao.select(
                new Where().add("id", WhereType.EQUAL, id),
                null,
                null,
                new Pagination().set(1));
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据主键查询
     *
     * @param id e
     * @return e
     */
    public Entity getById(Long id) {
        var where = new Where("id", WhereType.EQUAL, id);
        var pagination = new Pagination(1);
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        var list = baseDao.select(where, null, null, pagination);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据条件获取单个对象
     *
     * @param queryParam a
     * @return 查到多个则返回第一个 没有则返回 null
     */
    public Entity get(QueryParam queryParam) {
        queryParam.setPagination(1);
        if (!ScxConfig.realDelete()) {
            queryParam.where.add("tombstone", WhereType.EQUAL, false);
        }
        var list = baseDao.select(queryParam.where, queryParam.groupBy, queryParam.orderBy, queryParam.pagination);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据条件统计实体数 不提供模糊查询
     *
     * @param queryParam e
     * @return e
     */
    public Integer count(QueryParam queryParam) {
        if (!ScxConfig.realDelete()) {
            queryParam.where.add("tombstone", WhereType.EQUAL, false);
        }
        return baseDao.count(queryParam.where, null);
    }

    /**
     * 根据实体条件查询实体列表带 Like 条件 需要在实体类上注解@Like
     * 查询分页数据（提供模糊查询）
     *
     * @param queryParam e
     * @return e
     */
    public List<Entity> list(QueryParam queryParam) {
        if (!ScxConfig.realDelete()) {
            queryParam.where.add("tombstone", WhereType.EQUAL, false);
        }
        return baseDao.select(queryParam.where, queryParam.groupBy, queryParam.orderBy, queryParam.pagination);
    }

    /**
     * <p>listByIds.</p>
     *
     * @param ids a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listByIds(Long... ids) {
        var where = new Where("id", WhereType.IN, ids);
        if (!ScxConfig.realDelete()) {
            where.add("tombstone", WhereType.EQUAL, false);
        }
        return baseDao.select(where, null, null, null);
    }

    /**
     * 获取所有数据
     *
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listAll() {
        var where = ScxConfig.realDelete() ? null : new Where("tombstone", WhereType.EQUAL, false);
        var orderBy = new OrderBy("id", OrderByType.DESC);
        return baseDao.select(where, null, orderBy, null);
    }

    /**
     * 根据 field 获取 list 集合
     *
     * @param fieldName 字段名称
     * @return 以 value 为键值的 list 集合
     */
    public List<Map<String, Object>> getFieldList(String fieldName) {
        //确保查询字段在 数据库字段内 防止 sql 注入
        if (Arrays.stream(baseDao.tableInfo().allFields).filter(field -> field.getName().equals(fieldName)).count() == 1) {
            var sql = SQLBuilder.Select(baseDao.tableInfo().tableName).SelectColumns(new String[]{CaseUtils.toSnake(fieldName) + " As value "})
                    .Where(new Where(ScxConfig.realDelete() ? "" : " tombstone = FALSE")).GroupBy(new GroupBy("value")).GetSQL();
            return SQLRunner.query(sql, new HashMap<>());
        } else {
            return new ArrayList<>();
        }
    }

}
