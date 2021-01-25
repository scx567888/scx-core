package cool.scx.base;

import cool.scx.annotation.ScxMapping;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

public abstract class BaseService<Entity> {

    private final BaseDao<Entity> baseDao;

    @SuppressWarnings("unchecked")
    public BaseService() {
        var entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        baseDao = new BaseDao<>(entityClass);
    }

    /**
     * 实体插入新对象,并返回主键id值
     *
     * @param entity 实体
     * @return 实体
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Long save(Entity entity) {
        return baseDao.save(entity);
    }


    /**
     * 批量保存实体 (适用于少量数据 数据量 < 5000)
     *
     * @param entityList 实体集合
     * @return 插入成功的数据 自增主键
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public List<Long> saveList(List<Entity> entityList) {
        return baseDao.saveList(entityList);
    }

    /**
     * 删除指定id的实体
     *
     * @param ids 要删除的 id 集合
     * @return 被删除的数据条数 用于前台分页优化
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Integer deleteByIds(Long... ids) {
        return baseDao.deleteByIds(ids);
    }

    /**
     * 根据条件删除
     *
     * @param param e
     * @return e
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Integer delete(Param<Entity> param) {
        return baseDao.delete(param);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Entity update(Param<Entity> param) {
        return baseDao.update(param, false);
    }

    /**
     * 根据主键查询
     *
     * @param id e
     * @return e
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Entity getById(Long id) {
        return baseDao.getById(id);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Entity updateById(Entity entity) {
        return baseDao.update(new Param<>(entity), false);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Entity updateIncludeNull(Param<Entity> param) {
        return baseDao.update(param, true);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public List<Entity> listAll() {
        return baseDao.listAll();
    }

    /**
     * 根据实体条件查询实体列表带 Like 条件 需要在实体类上注解@Like
     * 查询分页数据（提供模糊查询）
     *
     * @param param e
     * @return e
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public List<Entity> list(Param<Entity> param) {
        return baseDao.list(param);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public List<Map<String, Object>> listMapAll() {
        return baseDao.listMapAll();
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public List<Map<String, Object>> listMap(Param<Entity> param) {
        return baseDao.listMap(param);
    }

    @ScxMapping(useMethodNameAsUrl = true)
    public Integer count(Param<Entity> param) {
        return baseDao.count(param, false);
    }

    /**
     * 根据条件统计实体数 不提供模糊查询
     *
     * @param param e
     * @return e
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Integer countIgnoreLike(Param<Entity> param) {
        return baseDao.count(param, true);
    }

    /**
     * 根据 field 获取 list 集合
     *
     * @param fieldName 字段名称
     * @return 以 value 为键值的 list 集合
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public List<Map<String, Object>> getFieldList(String fieldName) {
        return baseDao.getFieldList(fieldName);
    }

    /**
     * 根据条件获取单个对象
     *
     * @param param a
     * @return e
     */
    @ScxMapping(useMethodNameAsUrl = true)
    public Entity get(Param<Entity> param) {
        return baseDao.get(param);
    }

}
