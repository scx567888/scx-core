package cool.scx.base;

import cool.scx.boot.ScxConfig;
import cool.scx.enumeration.SortType;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class BaseService<Entity extends BaseModel> {

    private final BaseDao<Entity> baseDao;
    private final Class<Entity> entityClass;

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
    public Long save(Entity entity) {
        return baseDao.save(entity);
    }


    /**
     * 批量保存实体 (适用于少量数据 数据量 < 5000)
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
        var defaultParam = getDefaultParam();
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.delete(defaultParam);
    }

    /**
     * 根据条件删除
     *
     * @param param e
     * @return e
     */
    public Integer delete(Param<Entity> param) {
        return baseDao.delete(param);
    }

    /**
     * 根据主键查询
     *
     * @param id e
     * @return e
     */
    public Entity getById(Long id) {
        var defaultParam = getDefaultParam();
        defaultParam.setPagination(1).whereSql = "id = " + id;
        List<Entity> list = baseDao.list(defaultParam, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    public List<Entity> update(Param<Entity> param) {
        var ids = baseDao.update(param, false);
        var defaultParam = getDefaultParam();
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.list(defaultParam, false);
    }

    public Entity update(Entity entity) {
        var ids = baseDao.update(new Param<>(entity), false);
        return getById(ids.get(0));
    }

    /**
     * 根据 whereSql 更新 保护 null
     *
     * @param param 更新的参数
     * @return 更新后的数据
     */
    public List<Entity> updateIncludeNull(Param<Entity> param) {
        var ids = baseDao.update(param, true);
        var defaultParam = getDefaultParam();
        defaultParam.whereSql = " id IN (" + String.join(",", Stream.of(ids).map(String::valueOf).toArray(String[]::new)) + ")";
        return baseDao.list(defaultParam, false);
    }

    public Entity updateIncludeNull(Entity entity) {
        var ids = baseDao.update(new Param<>(entity), true);
        return getById(ids.get(0));
    }

    public List<Entity> listAll() {
        var param = getDefaultParam();
        param.addOrderBy("id", SortType.DESC);
        return list(param);
    }

    /**
     * 根据实体条件查询实体列表带 Like 条件 需要在实体类上注解@Like
     * 查询分页数据（提供模糊查询）
     *
     * @param param e
     * @return e
     */
    public List<Entity> list(Param<Entity> param) {
        if (!ScxConfig.realDelete) {
            param.queryObject.isDeleted = false;
        }
        return baseDao.list(param, false);
    }

    public List<Map<String, Object>> listMapAll() {
        var param = getDefaultParam();
        param.addOrderBy("id", SortType.DESC);
        return listMap(param);
    }

    public List<Map<String, Object>> listMap(Param<Entity> param) {
        if (!ScxConfig.realDelete) {
            param.queryObject.isDeleted = false;
        }
        return baseDao.listMap(param, false);
    }

    public Integer count(Param<Entity> param) {
        if (!ScxConfig.realDelete) {
            param.queryObject.isDeleted = false;
        }
        return baseDao.count(param, false);
    }

    /**
     * 根据条件统计实体数 不提供模糊查询
     *
     * @param param e
     * @return e
     */
    public Integer countIgnoreLike(Param<Entity> param) {
        if (!ScxConfig.realDelete) {
            param.queryObject.isDeleted = false;
        }
        return baseDao.count(param, true);
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

    /**
     * 根据条件获取单个对象
     *
     * @param param a
     * @return e
     */
    public Entity get(Param<Entity> param) {
        if (!ScxConfig.realDelete) {
            param.queryObject.isDeleted = false;
        }
        param.setPagination(1);
        var list = baseDao.list(param, false);
        return list.size() > 0 ? list.get(0) : null;
    }

    public Param<Entity> getDefaultParam() {
        Entity entity = null;
        try {
            entity = entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {
        }
        return new Param<>(entity);
    }

}
