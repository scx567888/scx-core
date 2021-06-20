package cool.scx.base;

import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.dao.BaseDao;
import cool.scx.enumeration.SortType;
import cool.scx.sql.SQLBuilder;
import cool.scx.sql.SQLRunner;
import cool.scx.util.CaseUtils;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

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
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        if (ScxConfig.realDelete()) {
            defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
            return baseDao.delete(defaultParam);
        } else {
            defaultParam.o.tombstone = true;
            defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ") AND tombstone = false";
            return baseDao.update(defaultParam, false).affectedLength;
        }
    }

    /**
     * 根据条件删除
     *
     * @param param e
     * @return e
     */
    public Integer delete(Param<Entity> param) {
        if (ScxConfig.realDelete()) {
            return baseDao.delete(param);
        } else {
            param.o.tombstone = true;
            return baseDao.update(param, false).affectedLength;
        }
    }

    /**
     * 批量删除
     *
     * @param entityList a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer deleteList(List<Entity> entityList) {
        var deleteCount = 0;
        if (ScxConfig.realDelete()) {
            for (Entity entity : entityList) {
                var defaultParam = new Param<>(entity);
                deleteCount += baseDao.delete(defaultParam);
            }
        } else {
            for (Entity entity : entityList) {
                var defaultParam = new Param<>(entity);
                defaultParam.o.tombstone = true;
                deleteCount += baseDao.update(defaultParam, false).affectedLength;
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
    public Integer revokeDeleteByIds(Long... ids) {
        if (ScxConfig.realDelete()) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            var defaultParam = new Param<>(ScxContext.getBean(entityClass));
            defaultParam.o.tombstone = false;
            defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
            return baseDao.update(defaultParam, false).affectedLength;
        }
    }

    /**
     * 根据条件恢复删除
     *
     * @param param e
     * @return e
     */
    public Integer revokeDelete(Param<Entity> param) {
        if (ScxConfig.realDelete()) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            param.o.tombstone = false;
            return baseDao.update(param, false).affectedLength;
        }
    }

    /**
     * 批量恢复数据
     *
     * @param entityList a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer revokeDeleteList(List<Entity> entityList) {
        var deleteCount = 0;
        if (ScxConfig.realDelete()) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            for (Entity entity : entityList) {
                var defaultParam = new Param<>(entity);
                defaultParam.o.tombstone = false;
                deleteCount += baseDao.update(defaultParam, false).affectedLength;
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
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.delete(defaultParam);
    }

    /**
     * 根据条件删除
     *
     * @param param e
     * @return e
     */
    public Integer deleteIgnoreConfig(Param<Entity> param) {
        return baseDao.delete(param);
    }

    /**
     * 批量删除 强制使用 物理删除
     *
     * @param entityList a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer deleteListIgnoreConfig(List<Entity> entityList) {
        var deleteCount = 0;
        for (Entity entity : entityList) {
            var defaultParam = new Param<>(entity);
            deleteCount += baseDao.delete(defaultParam);
        }
        return deleteCount;
    }

    /**
     * <p>update.</p>
     *
     * @param param a {@link cool.scx.bo.Param} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> update(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        var ids = baseDao.update(param, false);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.o.tombstone = ScxConfig.realDelete() ? null : false;
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.select(defaultParam, false);
    }

    /**
     * <p>update.</p>
     *
     * @param entity a Entity object.
     * @return a Entity object.
     */
    public Entity update(Entity entity) {
        var param = new Param<>(entity);
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        var ids = baseDao.update(param, false);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.o.tombstone = ScxConfig.realDelete() ? null : false;
        defaultParam.setPagination(1).whereSql = "id = " + entity.id;
        var list = baseDao.select(defaultParam, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据 whereSql 更新 保护 null
     *
     * @param param 更新的参数
     * @return 更新后的数据
     */
    public List<Entity> updateIncludeNull(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        var ids = baseDao.update(param, true);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.o.tombstone = ScxConfig.realDelete() ? null : false;
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.select(defaultParam, false);
    }

    /**
     * <p>updateIncludeNull.</p>
     *
     * @param entity a Entity object.
     * @return a Entity object.
     */
    public Entity updateIncludeNull(Entity entity) {
        var param = new Param<>(entity);
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        var ids = baseDao.update(param, true);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.o.tombstone = ScxConfig.realDelete() ? null : false;
        defaultParam.setPagination(1).whereSql = "id = " + ids.generatedKeys.get(0);
        var list = baseDao.select(defaultParam, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据主键查询
     *
     * @param id e
     * @return e
     */
    public Entity getById(Long id) {
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.whereSql = "id = " + id;
        return get(defaultParam);
    }

    /**
     * 根据条件获取单个对象
     *
     * @param param a
     * @return e
     */
    public Entity get(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        param.setPagination(1);
        var list = baseDao.select(param, true);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * <p>getWithLike.</p>
     *
     * @param param a {@link cool.scx.bo.Param} object.
     * @return a Entity object.
     */
    public Entity getWithLike(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        param.setPagination(1);
        var list = baseDao.select(param, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据条件统计实体数 不提供模糊查询
     *
     * @param param e
     * @return e
     */
    public Integer count(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        return baseDao.count(param, true);
    }

    /**
     * <p>countWithLike.</p>
     *
     * @param param a {@link cool.scx.bo.Param} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer countWithLike(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        return baseDao.count(param, false);
    }

    /**
     * 根据实体条件查询实体列表带 Like 条件 需要在实体类上注解@Like
     * 查询分页数据（提供模糊查询）
     *
     * @param param e
     * @return e
     */
    public List<Entity> list(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        return baseDao.select(param, true);
    }

    /**
     * <p>listByIds.</p>
     *
     * @param ids a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listByIds(Long... ids) {
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.o.tombstone = ScxConfig.realDelete() ? null : false;
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.select(defaultParam, true);
    }

    /**
     * 查询 包含 like
     *
     * @param param a {@link cool.scx.bo.Param} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listWithLike(Param<Entity> param) {
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        return baseDao.select(param, false);
    }

    /**
     * 获取所有数据
     *
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listAll() {
        var param = new Param<>(ScxContext.getBean(entityClass)).addOrderBy("id", SortType.DESC);
        param.o.tombstone = ScxConfig.realDelete() ? null : false;
        return baseDao.select(param, false);
    }

    /**
     * 根据 field 获取 list 集合
     *
     * @param fieldName 字段名称
     * @return 以 value 为键值的 list 集合
     */
    public List<Map<String, Object>> getFieldList(String fieldName) {
        if (Arrays.stream(baseDao.table().allFields).filter(field -> field.getName().equals(fieldName)).count() == 1) {
            var sql = SQLBuilder.Select(baseDao.table().tableName).SelectColumns(new String[]{CaseUtils.toSnake(fieldName) + " As value "})
                    .WhereSql(ScxConfig.realDelete() ? "" : " tombstone = FALSE").GroupBy(new HashSet<>() {{
                        add("value");
                    }}).GetSQL();
            return SQLRunner.query(sql, new HashMap<>());
        } else {
            return new ArrayList<>();
        }
    }

}
