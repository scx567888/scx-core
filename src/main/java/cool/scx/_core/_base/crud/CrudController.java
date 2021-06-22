package cool.scx._core._base.crud;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.BaseModel;
import cool.scx.base.BaseService;
import cool.scx.bo.QueryParam;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Method;
import cool.scx.enumeration.OrderByType;
import cool.scx.exception.BadRequestException;
import cool.scx.exception.CustomHttpRequestException;
import cool.scx.exception.HttpRequestException;
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
@ScxMapping("api")
public class CrudController {

    /**
     * 获取 service
     * todo 错误信息待处理
     *
     * @param modelName model 名称
     * @param <T>       model 类型
     * @return service
     * @throws HttpRequestException service 未找到
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseModel> BaseService<T> getBaseService(String modelName) throws HttpRequestException {
        try {
            var o = ScxContext.getBean(ScxContext.getClassByName(modelName.toLowerCase() + "service"));
            return (BaseService<T>) o;
        } catch (Exception e) {
            throw new CustomHttpRequestException(ctx -> Json.fail("unknown-crud-service").put("service-name", modelName.toLowerCase()).sendToClient(ctx));
        }
    }

    private static BaseModel getBaseModel(Map<String, Object> entityMap, String modelName) throws HttpRequestException {
        try {
            return (BaseModel) ObjectUtils.mapToBean(entityMap, ScxContext.getClassByName(modelName));
        } catch (Exception e) {
            //这里一般就是 参数转换错误
            throw new BadRequestException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseModel> QueryParam getParam(String modelName, Integer limit, Integer page, String orderByColumn, String sortType, Map<String, Object> queryObject) {
        var modelClass = (Class<T>) ScxContext.getClassByName(modelName);
        T o = ObjectUtils.mapToBean(queryObject, modelClass);
        if (o == null) {
            try {
                o = modelClass.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {

            }
        }
        var p = new QueryParam();
        if (limit != null && limit != -1) {
            p.setPagination(page, limit);
        }
        if (orderByColumn != null) {
            if (sortType == null || "desc".equals(sortType)) {
                p.addOrderBy(orderByColumn, OrderByType.DESC);
            } else {
                p.addOrderBy(orderByColumn, OrderByType.ASC);
            }
        }
        return p;
    }


    /**
     * 列表查询
     *
     * @param modelName     a {@link java.lang.String} object.
     * @param limit         a {@link java.lang.Integer} object.
     * @param page          a {@link java.lang.Integer} object.
     * @param orderByColumn a {@link java.lang.String} object.
     * @param sortType      a {@link java.lang.String} object.
     * @param queryObject   a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = ":modelName/list", method = {Method.GET, Method.POST})
    public Json list(String modelName,
                     @FromBody("limit") Integer limit,
                     @FromBody("page") Integer page,
                     @FromBody("orderBy.orderByColumn") String orderByColumn,
                     @FromBody("orderBy.sortType") String sortType,
                     @FromBody("queryObject") Map<String, Object> queryObject
    ) throws HttpRequestException {
        var baseService = getBaseService(modelName);
        var param = getParam(modelName, limit, page, orderByColumn, sortType, queryObject);
        var list = baseService.list(param);
        var count = baseService.count(param);
        return Json.ok().put("items", list).put("total", count);
    }


    /**
     * 获取详细信息
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Long} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = ":modelName/:id", method = Method.GET)
    public Json info(String modelName, Long id) throws HttpRequestException {
        var baseService = getBaseService(modelName);
        var info = baseService.getById(id);
        return Json.ok().put("info", info);
    }

    /**
     * 保存
     *
     * @param modelName a {@link java.lang.String} object.
     * @param entityMap a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = ":modelName", method = Method.POST)
    public Json save(String modelName, Map<String, Object> entityMap) throws HttpRequestException {
        var baseService = getBaseService(modelName);
        var realObject = getBaseModel(entityMap, modelName);
        return Json.ok().put("item", baseService.save(realObject));
    }

    /**
     * 更新
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
        return Json.ok().put("item", baseService.update(realObject));
    }

    /**
     * 删除
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
        return Json.ok().put("delete-result", deleteByIds == 1);
    }

    /**
     * 批量删除
     *
     * @param modelName a {@link java.lang.String} object.
     * @param deleteIds a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = ":modelName/batch-delete", method = Method.DELETE)
    public Json batchDelete(String modelName, @FromBody("deleteIds") List<Long> deleteIds) throws HttpRequestException {
        var baseService = getBaseService(modelName);
        var deletedCount = baseService.deleteByIds(deleteIds.toArray(Long[]::new));
        return Json.ok().put("deletedCount", deletedCount);
    }

    /**
     * 撤销删除
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = ":modelName/revoke-delete/:id", method = Method.GET)
    public Json revokeDelete(String modelName, Integer id) throws HttpRequestException {
        var baseService = getBaseService(modelName);
        var revokeDeleteCount = baseService.revokeDeleteByIds(Long.valueOf(id));
        return revokeDeleteCount == 1 ? Json.ok() : Json.fail();
    }

    /**
     * 获取自动完成字段
     *
     * @param modelName a {@link java.lang.String} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = ":modelName/get-auto-complete/:fieldName", method = Method.POST)
    public Json getAutoComplete(String modelName, String fieldName) throws HttpRequestException {
        var baseService = getBaseService(modelName);
        var fieldList = baseService.getFieldList(fieldName);
        return Json.ok().put("fields", fieldList);
    }

    /**
     * 校验唯一性
     *
     * @param modelName a {@link java.lang.String} object.
     * @param params    a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = ":modelName/check-unique", method = Method.POST)
    public Json checkUnique(String modelName, Map<String, Object> params) throws HttpRequestException {
        var baseService = getBaseService(modelName);
        var param = getParam(modelName, null, null, null, null, params);
//        if (param.o.id != null) {
//            param.whereSql = "id != " + param.o.id;
//        }
//        param.o.id = null;
        var b = baseService.count(param) == 0;
        return Json.ok().put("isUnique", b);
    }

}
