package cool.scx.base;

import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.enumeration.SortType;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * <p>Abstract BaseService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public abstract class BaseService<Entity extends BaseModel> {

    private final BaseDao<Entity> baseDao;
    private final Class<Entity> entityClass;

    @SuppressWarnings("unchecked")
    /**
     * <p>Constructor for BaseService.</p>
     */
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
        var newId = baseDao.save(entity);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.setPagination(1).whereSql = "id = " + newId;
        List<Entity> list = baseDao.list(defaultParam, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 批量保存实体
     *
     * @param entityList 实体集合
     * @return 插入成功的数据 自增主键
     */
    public List<Long> saveList(List<Entity> entityList) {
        return baseDao.saveList(entityList);
    }

    /**
     * 删除指定id的实体
     *
     * @param ids 要删除的 id 集合
     * @return 被删除的数据条数 用于前台分页优化
     */
    public Integer deleteByIds(Long... ids) {
        if (ScxConfig.realDelete) {
            var defaultParam = new Param<>(ScxContext.getBean(entityClass));
            defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
            return baseDao.delete(defaultParam);
        } else {
            var defaultParam = new Param<>(ScxContext.getBean(entityClass));
            defaultParam.queryObject.isDeleted = true;
            defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ") AND is_deleted = false";
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
        if (ScxConfig.realDelete) {
            return baseDao.delete(param);
        } else {
            param.queryObject.isDeleted = true;
            return baseDao.update(param, false).affectedLength;
        }

    }

    /**
     * <p>deleteList.</p>
     *
     * @param entityList a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer deleteList(List<Entity> entityList) {
        var deleteCount = 0;
        if (ScxConfig.realDelete) {
            for (Entity entity : entityList) {
                var defaultParam = new Param<>(entity);
                deleteCount += baseDao.delete(defaultParam);
            }
        } else {
            for (Entity entity : entityList) {
                var defaultParam = new Param<>(entity);
                defaultParam.queryObject.isDeleted = true;
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
        if (ScxConfig.realDelete) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            var defaultParam = new Param<>(ScxContext.getBean(entityClass));
            defaultParam.queryObject.isDeleted = false;
            defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
            return baseDao.update(defaultParam, false).affectedLength;
        }
    }

    /**
     * 根据条件删除
     *
     * @param param e
     * @return e
     */
    public Integer revokeDelete(Param<Entity> param) {
        if (ScxConfig.realDelete) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            param.queryObject.isDeleted = false;
            return baseDao.update(param, false).affectedLength;
        }

    }

    /**
     * <p>revokeDeleteList.</p>
     *
     * @param entityList a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer revokeDeleteList(List<Entity> entityList) {
        var deleteCount = 0;
        if (ScxConfig.realDelete) {
            throw new RuntimeException("物理删除模式下不允许恢复删除!!!");
        } else {
            for (Entity entity : entityList) {
                var defaultParam = new Param<>(entity);
                defaultParam.queryObject.isDeleted = false;
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
     * <p>deleteListIgnoreConfig.</p>
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
     * @param param a {@link cool.scx.base.Param} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> update(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        var ids = baseDao.update(param, false);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.list(defaultParam, false);
    }

    /**
     * <p>update.</p>
     *
     * @param entity a Entity object.
     * @return a Entity object.
     */
    public Entity update(Entity entity) {
        var param = new Param<>(entity);
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        var ids = baseDao.update(param, false);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        defaultParam.setPagination(1).whereSql = "id = " + ids.generatedKeys.get(0);
        var list = baseDao.list(defaultParam, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据 whereSql 更新 保护 null
     *
     * @param param 更新的参数
     * @return 更新后的数据
     */
    public List<Entity> updateIncludeNull(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        var ids = baseDao.update(param, true);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.list(defaultParam, false);
    }

    /**
     * <p>updateIncludeNull.</p>
     *
     * @param entity a Entity object.
     * @return a Entity object.
     */
    public Entity updateIncludeNull(Entity entity) {
        var param = new Param<>(entity);
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        var ids = baseDao.update(param, true);
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        defaultParam.setPagination(1).whereSql = "id = " + ids.generatedKeys.get(0);
        var list = baseDao.list(defaultParam, false);
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
        defaultParam.setPagination(1).whereSql = "id = " + id;
        defaultParam.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        var list = baseDao.list(defaultParam, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据条件获取单个对象
     *
     * @param param a
     * @return e
     */
    public Entity get(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        param.setPagination(1);
        var list = baseDao.list(param, true);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * <p>getWithLike.</p>
     *
     * @param param a {@link cool.scx.base.Param} object.
     * @return a Entity object.
     */
    public Entity getWithLike(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        param.setPagination(1);
        var list = baseDao.list(param, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据条件统计实体数 不提供模糊查询
     *
     * @param param e
     * @return e
     */
    public Integer count(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        return baseDao.count(param, true);
    }

    /**
     * <p>countWithLike.</p>
     *
     * @param param a {@link cool.scx.base.Param} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer countWithLike(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
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
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        return baseDao.list(param, true);
    }

    /**
     * <p>listByIds.</p>
     *
     * @param ids a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listByIds(Long... ids) {
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.list(defaultParam, true);
    }

    /**
     * <p>listWithLike.</p>
     *
     * @param param a {@link cool.scx.base.Param} object.
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listWithLike(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        return baseDao.list(param, false);
    }

    /**
     * <p>listMap.</p>
     *
     * @param param a {@link cool.scx.base.Param} object.
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, Object>> listMap(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        return baseDao.listMap(param, true);
    }

    /**
     * <p>listMapByIds.</p>
     *
     * @param ids a {@link java.lang.Long} object.
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, Object>> listMapByIds(Long... ids) {
        var defaultParam = new Param<>(ScxContext.getBean(entityClass));
        defaultParam.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.listMap(defaultParam, true);
    }

    /**
     * <p>listMapWithLike.</p>
     *
     * @param param a {@link cool.scx.base.Param} object.
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, Object>> listMapWithLike(Param<Entity> param) {
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        return baseDao.listMap(param, false);
    }

    /**
     * <p>listAll.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Entity> listAll() {
        var param = new Param<>(ScxContext.getBean(entityClass)).addOrderBy("id", SortType.DESC);
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        return baseDao.list(param, false);
    }

    /**
     * <p>listMapAll.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, Object>> listMapAll() {
        var param = new Param<>(ScxContext.getBean(entityClass)).addOrderBy("id", SortType.DESC);
        param.queryObject.isDeleted = ScxConfig.realDelete ? null : false;
        return baseDao.listMap(param, false);
    }

    /**
     * 根据 field 获取 list 集合
     *
     * @param fieldName 字段名称
     * @return 以 value 为键值的 list 集合
     */
    public List<Map<String, Object>> getFieldList(String fieldName) {
        return baseDao.getFieldList(fieldName);
    }

}
