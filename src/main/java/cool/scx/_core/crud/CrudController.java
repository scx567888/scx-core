package cool.scx._core.crud;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.BaseModel;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Method;
import cool.scx.enumeration.SortType;
import cool.scx.exception.HttpResponseException;
import cool.scx.util.ObjectUtils;
import cool.scx.vo.Json;

import java.util.List;
import java.util.Map;

/**
 * 通用 Crud的 controller
 *
 * @author 司昌旭
 * @version 1.0.10
 */
@ScxController("api")
public class CrudController {

    /**
     * 获取 service
     *
     * @param modelName model 名称
     * @param <T>       model 类型
     * @return service
     * @throws HttpResponseException service 未找到
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseModel> BaseService<T> getBaseService(String modelName) throws HttpResponseException {
        try {
            var o = ScxContext.getBean(ScxContext.getClassByName(modelName.toLowerCase() + "service"));
            return (BaseService<T>) o;
        } catch (Exception e) {
            throw new HttpResponseException(ctx -> Json.fail(modelName.toLowerCase() + "service : 不存在!!!").sendToClient(ctx));
        }
    }

    private static BaseModel getBaseModel(Map<String, Object> entityMap, String modelName) throws HttpResponseException {
        try {
            return (BaseModel) ObjectUtils.mapToBean(entityMap, ScxContext.getClassByName(modelName));
        } catch (Exception e) {
            e.printStackTrace();
            //这里一般就是 参数转换错误
            throw new HttpResponseException(routingContext -> Json.fail(Json.SYSTEM_ERROR, "参数错误!!!").sendToClient(routingContext));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseModel> Param<T> getParam(String modelName, Integer limit, Integer page, String orderByColumn, String sortType, Map<String, Object> queryObject) {
        var modelClass = (Class<T>) ScxContext.getClassByName(modelName);
        T o = ObjectUtils.mapToBean(queryObject, modelClass);
        if (o == null) {
            try {
                o = modelClass.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {

            }
        }
        var p = new Param<>(o);
        if (limit != null && limit != -1) {
            p.setPagination(page, limit);
        }
        if (orderByColumn != null) {
            if (sortType == null || "desc".equals(sortType)) {
                p.addOrderBy(orderByColumn, SortType.DESC);
            } else {
                p.addOrderBy(orderByColumn, SortType.ASC);
            }
        }
        return p;
    }


    /**
     * <p>list.</p>
     *
     * @param modelName     a {@link java.lang.String} object.
     * @param limit         a {@link java.lang.Integer} object.
     * @param page          a {@link java.lang.Integer} object.
     * @param orderByColumn a {@link java.lang.String} object.
     * @param sortType      a {@link java.lang.String} object.
     * @param queryObject   a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = ":modelName/list", method = {Method.GET, Method.POST})
    public Json list(String modelName,
                     @FromBody("limit") Integer limit,
                     @FromBody("page") Integer page,
                     @FromBody("orderBy.orderByColumn") String orderByColumn,
                     @FromBody("orderBy.sortType") String sortType,
                     @FromBody("queryObject") Map<String, Object> queryObject
    ) throws HttpResponseException {
        var baseService = getBaseService(modelName);
        var param = getParam(modelName, limit, page, orderByColumn, sortType, queryObject);
        var list = baseService.listWithLike(param);
        var count = baseService.countWithLike(param);
        return Json.ok().tables(list, count);
    }


    /**
     * <p>info.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Long} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = ":modelName/:id", method = Method.GET)
    public Json info(String modelName, Long id) throws HttpResponseException {
        var baseService = getBaseService(modelName);
        var list = baseService.getById(id);
        return Json.ok().items(list);
    }

    /**
     * <p>save.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param entityMap a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = ":modelName", method = Method.POST)
    public Json save(String modelName, Map<String, Object> entityMap) throws HttpResponseException {
        var baseService = getBaseService(modelName);
        var realObject = getBaseModel(entityMap, modelName);
        return Json.ok().items(baseService.save(realObject));
    }

    /**
     * <p>update.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param entityMap a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.lang.Exception if any.
     */
    @ScxMapping(value = ":modelName", method = Method.PUT)
    public Json update(String modelName, Map<String, Object> entityMap) throws Exception {
        var baseService = getBaseService(modelName);
        var realObject = getBaseModel(entityMap, modelName);
        return Json.ok().items(baseService.update(realObject));
    }

    /**
     * <p>delete.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.lang.Exception if any.
     */
    @ScxMapping(value = ":modelName/:id", method = Method.DELETE)
    public Json delete(String modelName, Integer id) throws Exception {
        var baseService = getBaseService(modelName);
        var deleteByIds = baseService.deleteByIds(Long.valueOf(id));
        return Json.ok().items(deleteByIds == 1);
    }

    /**
     * <p>batchDelete.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param deleteIds a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = ":modelName/batchDelete", method = Method.DELETE)
    public Json batchDelete(String modelName, @FromBody("deleteIds") List<Long> deleteIds) throws HttpResponseException {
        var baseService = getBaseService(modelName);
        var deletedCount = baseService.deleteByIds(deleteIds.toArray(Long[]::new));
        return Json.ok("success").data("deletedCount", deletedCount);
    }

    /**
     * <p>revokeDelete.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = ":modelName/revokeDelete/:id", method = Method.GET)
    public Json revokeDelete(String modelName, Integer id) throws HttpResponseException {
        var baseService = getBaseService(modelName);
        var revokeDeleteCount = baseService.revokeDeleteByIds(Long.valueOf(id));
        return Json.ok(revokeDeleteCount == 1 ? "success" : "error");
    }

    /**
     * <p>getAutoComplete.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = ":modelName/getAutoComplete/:fieldName", method = Method.POST)
    public Json getAutoComplete(String modelName, String fieldName) throws HttpResponseException {
        var baseService = getBaseService(modelName);
        var fieldList = baseService.getFieldList(fieldName);
        return Json.ok().items(fieldList);
    }

    /**
     * <p>checkUnique.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param params    a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = ":modelName/checkUnique", method = Method.POST)
    public Json checkUnique(String modelName, Map<String, Object> params) throws HttpResponseException {
        var baseService = getBaseService(modelName);
        var param = getParam(modelName, null, null, null, null, params);
        if (param.queryObject.id != null) {
            param.whereSql = "id != " + param.queryObject.id;
        }
        param.queryObject.id = null;
        var b = baseService.count(param) == 0;
        return Json.ok().data("isUnique", b);
    }

}
