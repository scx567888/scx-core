package cool.scx.base;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

public abstract class BaseService<Entity> {

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
        return baseDao.deleteByIds(ids);
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

    public Entity update(Param<Entity> param) {
        return baseDao.update(param, false);
    }

    /**
     * 根据主键查询
     *
     * @param id e
     * @return e
     */
    public Entity getById(Long id) {
        return baseDao.getById(id);
    }

    public Entity updateById(Entity entity) {
        return baseDao.update(new Param<>(entity), false);
    }

    public Entity updateIncludeNull(Param<Entity> param) {
        return baseDao.update(param, true);
    }

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
    public List<Entity> list(Param<Entity> param) {
        return baseDao.list(param);
    }

    public List<Map<String, Object>> listMapAll() {
        return baseDao.listMapAll();
    }


    public List<Map<String, Object>> listMap(Param<Entity> param) {
        return baseDao.listMap(param);
    }

    public Integer count(Param<Entity> param) {
        return baseDao.count(param, false);
    }

    /**
     * 根据条件统计实体数 不提供模糊查询
     *
     * @param param e
     * @return e
     */
    public Integer countIgnoreLike(Param<Entity> param) {
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
        return baseDao.get(param);
    }

}
