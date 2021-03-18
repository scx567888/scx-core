package cool.scx.base;

import cool.scx.annotation.*;
import cool.scx.bo.FileUpload;
import cool.scx.bo.Param;
import cool.scx.business.uploadfile.UploadFile;
import cool.scx.business.uploadfile.UploadFileService;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Method;
import cool.scx.enumeration.SortType;
import cool.scx.exception.HttpResponseException;
import cool.scx.util.LogUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.file.FileUtils;
import cool.scx.vo.Download;
import cool.scx.vo.Image;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通用 controller
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController("api")
public class BaseController {

    private final UploadFileService uploadFileService;

    /**
     * BaseController 构造函数
     *
     * @param uploadFileService 会自动注入
     */
    public BaseController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }


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

    @SuppressWarnings("unchecked")
    private <T extends BaseModel> Param<T> getParam(String modelName, Integer limit, Integer page, String orderByColumn, String sortType, Map<String, Object> queryObject) {
        var modelClass = (Class<T>) ScxContext.getClassByName(modelName);
        var p = new Param<>(ObjectUtils.mapToBeanNotNull(queryObject, modelClass));
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
        var realObject = (BaseModel) ObjectUtils.mapToBean(entityMap, ScxContext.getClassByName(modelName));
        var newObjectId = baseService.save(realObject).id;
        var newObject = baseService.getById(newObjectId);
        return Json.ok().items(newObject);
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
        var realObject = (BaseModel) ObjectUtils.mapToBean(entityMap, ScxContext.getClassByName(modelName));
        var newObj = baseService.update(realObject);
        return Json.ok().items(newObj);
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


    /**
     * 通用下载资源方法
     *
     * @param year      year
     * @param month     month
     * @param day       day
     * @param hour      hour
     * @param timestamp timestamp
     * @param fileName  要下载的文件名
     * @param ctx       a {@link io.vertx.ext.web.RoutingContext} object.
     * @return a {@link cool.scx.vo.Download} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     * @throws java.io.UnsupportedEncodingException     if any.
     */
    @ScxMapping(value = "/download/:year/:month/:day/:hour/:timestamp/:fileName", method = Method.GET)
    public Download download(String year, String month, String day, String hour, String timestamp, String fileName, RoutingContext ctx) throws HttpResponseException, UnsupportedEncodingException {
        var file = new File(ScxConfig.uploadFilePath() + "/" + year + "/" + month + "/" + day + "/" + hour + "/" + timestamp + "/" + fileName);
        if (!file.exists()) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404).send("要下载的文件不存在或已被删除!!!"));
        }
        LogUtils.recordLog("ip 为 :" + NetUtils.getIpAddr() + "的用户 下载了" + fileName);
        //  这里让文件限速到 500 kb
        return new Download(file, file.getName(), 512000L);
    }

    /**
     * 通用查看图片方法
     *
     * @param year      year
     * @param month     month
     * @param day       day
     * @param hour      hour
     * @param timestamp timestamp
     * @param fileName  要下载的文件名
     * @param width     a {@link java.lang.Integer} object.
     * @param height    a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.vo.Binary} object.
     */
    @ScxMapping(value = "/showPicture/:year/:month/:day/:hour/:timestamp/:fileName", method = Method.GET)
    public Image showPicture(String year, String month, String day, String hour, String timestamp, String fileName, @FromQuery("w") Integer width, @FromQuery("h") Integer height) {
        return new Image(new File(ScxConfig.uploadFilePath() + "/" + year + "/" + month + "/" + day + "/" + hour + "/" + timestamp + "/" + fileName), width, height);
    }

    /**
     * 通用查看图片方法
     *
     * @param id     要显示的图片 id
     *               下载文件或错误
     * @param width  a {@link java.lang.Integer} object.
     * @param height a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.vo.Binary} object.
     */
    @ScxMapping("/showPictureById/:id")
    public Image showPictureById(@FromPath Long id, @FromQuery("w") Integer width, @FromQuery("h") Integer height) {
        return new Image(ScxConfig.uploadFilePath() + "/" + uploadFileService.getById(id).filePath, width, height);
    }


    /**
     * 单个文件上传 和 分片文件上传
     *
     * @param fileName      文件名
     * @param fileSize      文件大小
     * @param fileMD5       文件md5
     * @param chunkLength   分片总长度
     * @param nowChunkIndex 当前分片
     * @param fileData      文件内容
     * @return r
     */
    @ScxMapping(value = "/upload", method = Method.POST)
    public Json upload(String fileName, Long fileSize, String fileMD5, Integer chunkLength, Integer nowChunkIndex, FileUpload fileData) {
        //先判断 文件是否已经上传过
        if (StringUtils.isNotEmpty(fileMD5)) {
            UploadFile fileByMd5 = uploadFileService.findFileByMd5(fileMD5);
            //证明有其他人上传过此文件 就不上传了 直接 返回文件上传成功的信息给用户
            if (fileByMd5 != null) {
                var uploadFile = new UploadFile();
                uploadFile.fileId = StringUtils.getUUID();
                uploadFile.fileName = fileName;
                uploadFile.uploadTime = LocalDateTime.now();
                uploadFile.filePath = fileByMd5.filePath;
                uploadFile.fileSizeDisplay = fileByMd5.fileSizeDisplay;
                uploadFile.fileSize = fileByMd5.fileSize;
                uploadFile.fileMD5 = fileByMd5.fileMD5;
                var save = uploadFileService.save(uploadFile);
                return Json.ok().data("type", "uploadSuccess").items(save);
            }
        }

        //单文件上传
        if (chunkLength == 1) {
            var uploadFile = UploadFile.getNewUpload(fileName, fileSize, fileMD5);
            var fileStoragePath = ScxConfig.uploadFilePath().getPath() + "\\" + uploadFile.filePath;
            Boolean aBoolean = FileUtils.fileAppend(fileStoragePath, fileData.buffer.getBytes());
            //文件存储成功 将文件信息写入数据库
            if (aBoolean) {
                var save = uploadFileService.save(uploadFile);
                return Json.ok().data("type", "uploadSuccess").items(save);
            } else {
                return Json.ok().data("type", "uploadFail");
            }
        } else {
            //分片文件上传
            //先获取已经上传的块
            //当前文件上传的临时标识 可以在这里面获取文件上传的信息 包括上传到多少
            //没传完呢
            var uploadTempFile = new File(ScxConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "\\.scxTemp");
            var uploadConfigFile = new File(ScxConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "\\.scxUpload");
            if (nowChunkIndex < chunkLength) {
                var lastUploadChunk = FileUtils.getLastUploadChunk(uploadConfigFile, chunkLength);
                if (lastUploadChunk >= nowChunkIndex) {
                    return Json.ok().data("type", "needMore").items(nowChunkIndex);
                } else {
                    Boolean aBoolean = FileUtils.fileAppend(uploadTempFile.getPath(), fileData.buffer.getBytes());
                    FileUtils.changeLastUploadChunk(uploadConfigFile, nowChunkIndex);
                    return Json.ok().data("type", "needMore").items(nowChunkIndex + 1);
                }
            } else {
                //传完了
                //分片文件全部上传完成
                //移动临时文件 向数据库写数据 清楚残留文件
                var uploadFile = UploadFile.getNewUpload(fileName, fileSize, fileMD5);
                var fileStoragePath = ScxConfig.uploadFilePath().getPath() + "\\" + uploadFile.filePath;
                FileUtils.fileAppend(uploadTempFile.getPath(), fileData.buffer.getBytes());
                boolean b = uploadTempFile.renameTo(new File(fileStoragePath));
                if (b) {
                    FileUtils.deleteFileByPath(uploadTempFile.getParent());
                    var save = uploadFileService.save(uploadFile);
                    return Json.ok().data("type", "uploadSuccess").items(save);

                } else {
                    return Json.ok().data("type", "uploadFail");
                }
            }

        }
    }

}
