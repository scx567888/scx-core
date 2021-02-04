package cool.scx.base;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.business.system.ScxLogService;
import cool.scx.business.uploadfile.UploadFile;
import cool.scx.business.uploadfile.UploadFileService;
import cool.scx.enumeration.HttpMethod;
import cool.scx.enumeration.SortType;
import cool.scx.util.FileUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.ObjectUtils;
import cool.scx.vo.Json;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Map;

@ScxController("api")
public class BaseController {

    private final ScxLogService scxLogService;

    private final UploadFileService uploadFileService;

    public BaseController(ScxLogService scxLogService, UploadFileService uploadFileService) {
        this.scxLogService = scxLogService;
        this.uploadFileService = uploadFileService;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseModel> Param<T> getParam(String modelName, Map<String, Object> params) {
        var modelClass = (Class<T>) ScxContext.getBaseModelClassByName(modelName);
        Param<T> p = new Param<>(ObjectUtils.mapToBean(params, modelClass));
        p.setPagination(1000);
        p.addOrderBy("id", SortType.DESC);
        p.addGroupBy("level");
        return p;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseModel> BaseService<T> getBaseService(String modelName) {
        return (BaseService<T>) ScxContext.getBaseServiceByName(modelName.toLowerCase() + "service");
    }

    //
    @ScxMapping(value = ":modelName/list", httpMethod = HttpMethod.POST)
    public Json list(String modelName, Map<String, Object> params) {
        var baseService = getBaseService(modelName);
        var param = getParam(modelName, params);
        var list = baseService.list(param);
        var count = baseService.count(param);
        return Json.ok().tables(list, count);
    }

    @ScxMapping(value = ":modelName/:id", httpMethod = HttpMethod.GET)
    public Json info(String modelName, Integer id) {
        var baseService = getBaseService(modelName);
        var list = baseService.getById(Long.valueOf(id));
        return Json.ok().items(list);
    }

    @ScxMapping(value = ":modelName", httpMethod = HttpMethod.POST)
    public Json save(String modelName, Map<String, Object> entityMap) {
        var baseService = getBaseService(modelName);
        var realObject = (BaseModel) ObjectUtils.mapToBean(entityMap, ScxContext.getBaseModelClassByName(modelName));
        var newObjectId = baseService.save(realObject).id;
        var newObject = baseService.getById(newObjectId);
        return Json.ok().items(newObject);
    }

    @ScxMapping(value = ":modelName", httpMethod = HttpMethod.PUT)
    public Json update(String modelName, Map<String, Object> entityMap) throws Exception {
        var baseService = getBaseService(modelName);
        var realObject = (BaseModel) ObjectUtils.mapToBean(entityMap, ScxContext.getBaseModelClassByName(modelName));
        var newObj = baseService.update(realObject);
        return Json.ok().items(newObj);
    }

    @ScxMapping(value = ":modelName/:id", httpMethod = HttpMethod.DELETE)
    public Json delete(String modelName, Integer id) throws Exception {
        var baseService = getBaseService(modelName);
        var deleteByIds = baseService.deleteByIds(Long.valueOf(id));
        return Json.ok().items(deleteByIds == 1);
    }

    @ScxMapping(value = ":modelName/batchDelete", httpMethod = HttpMethod.DELETE)
    public Json batchDelete(String modelName, Map<String, Object> params) {
        var deleteIds = (Long[]) params.get("deleteIds");
        var baseService = getBaseService(modelName);
        var deletedCount = baseService.deleteByIds(deleteIds);
        return Json.ok("success").data("deletedCount", deletedCount);
    }

    @ScxMapping(value = ":modelName/revokeDelete/:id", httpMethod = HttpMethod.GET)
    public Json revokeDelete(String modelName, Integer id) {
        var baseService = getBaseService(modelName);
        var revokeDeleteCount = baseService.revokeDeleteByIds(Long.valueOf(id));
        return Json.ok(revokeDeleteCount == 1 ? "success" : "error");
    }

    @ScxMapping(value = ":modelName/getAutoComplete/:fieldName", httpMethod = HttpMethod.POST)
    public Json getAutoComplete(String modelName, String fieldName) {
        var baseService = getBaseService(modelName);
        var fieldList = baseService.getFieldList(fieldName);
        return Json.ok().items(fieldList);
    }

    @ScxMapping(value = ":modelName/checkUnique", httpMethod = HttpMethod.POST)
    public Json checkUnique(String modelName, Map<String, Object> params) {
        var baseService = getBaseService(modelName);
        var param = getParam(modelName, params);
        param.whereSql = "id != " + param.queryObject.id;
        param.queryObject.id = null;
        var b = baseService.count(param) == 0;
        return Json.ok().data("isUnique", b);
    }

    /**
     * 通用下载资源方法
     *
     * @param fileName 要下载的文件名
     */
    @ScxMapping("/download/:year/:month/:day/:hour/:timestamp/:fileName")
    public void download(String year, String month, String day, String hour, String timestamp, String fileName) throws UnsupportedEncodingException {
        var fullPath = ScxConfig.uploadFilePath + year + "/" + month + "/" + day + "/" + hour + "/" + timestamp + "/" + fileName;
        //FileUtils.downloadFile(response, request, fullPath);
        scxLogService.outAndRecordLog("ip 为 :" + NetUtils.getIpAddr() + "的用户 下载了" + fileName);
    }


    /**
     * 通用查看图片方法
     *
     * @param fileName 要下载的文件名
     *                 下载文件或错误
     */
    @ScxMapping("/showPicture/:year/:month:/:day/:hour/:timestamp/:fileName")
    public void showPicture(String year,
                            String month,
                            String day,
                            String hour,
                            String timestamp,
                            String fileName) {
        var filePath = ScxConfig.uploadFilePath + year + "/" + month + "/" + day + "/" + hour + "/" + timestamp + "/" + fileName;
        //var width = request.getParameter("w") != null ? Integer.parseInt(request.getParameter("w")) : null;
        //var height = request.getParameter("h") != null ? Integer.parseInt(request.getParameter("h")) : null;
        //FileUtils.showPicture(response, new File(filePath), width, height);
    }


    /**
     * 通用查看图片方法
     *
     * @param id 要显示的图片 id
     *           下载文件或错误
     */
    @ScxMapping("/showPictureById/:id")
    public void showPictureById(Long id) {
        var uploadFile = uploadFileService.getById(id);
        //var width = request.getParameter("w") != null ? Integer.parseInt(request.getParameter("w")) : null;
        //var height = request.getParameter("h") != null ? Integer.parseInt(request.getParameter("h")) : null;
        //FileUtils.showPicture(response, new File(ScxContext.uploadFilePath + uploadFile.filePath), width, height);
    }


    /**
     * 单个文件上传 和 分片文件上传
     *
     * @param file     文件
     * @param fileName 文件名
     * @return 文件保存的路径
     */
    @ScxMapping("/upload")
    public Json upload(File file,
                       String fileName,
                       String fileSize,
                       Integer chunksNumber,
                       Integer chunk,
                       Integer type) {
        //if ("".equals(fileName)) {
        //    //fileName = file.getOriginalFilename();
        //}
        ////文件上传类型 0 为单文件 1 为分片文件
        //if (type == 0) {
        //    String fileWritePath = FileUtils.getDateStr() + '/' + fileName;
        //    var b = FileUtils.uploadFile(file, fileWritePath, -1, -1);
        //    if (b) {
        //        //保存文件信息
        //        var u = new UploadFile();
        //        u.fileName = fileName;
        //        u.filePath = fileWritePath;
        //        u.uploadTime = new Date();
        //        u.fileSize = fileSize;
        //        u.id = uploadFileService.save(u);
        //        return Json.ok().items(u);
        //    } else {
        //        return Json.fail("上传失败");
        //    }
        //} else {
        //    var b = FileUtils.uploadFile(file, fileName, chunk, chunksNumber);
        //    if (b) {
        //        //当前分片上传成功 返回下一个请求的分片
        //        return Json.ok().data("chunk", chunk + 1);
        //    } else {
        //        return Json.ok().data("chunk", -1);
        //    }
        //}
        return Json.ok();
    }


    /**
     * 校验文件  和 获取分割文件上传最后一次的分块
     *
     * @param params 文件名
     * @return 文件校验结果 true 为合并并校验成功
     */
    @ScxMapping("/upload/validateFile")
    public Json uploadValidateFile(Map<String, Object> params) {
        //获取文件名
        String fileName = (String) params.get("fileName");
        //获取文件大小
        String fileSize = (String) params.get("fileSize");
        //获取类型 0 代表文件要进行上传操作 返回最后一次上传的区块 1代表全部上传完成 进行最后一部操作
        Integer type = (Integer) params.get("type");
        //返回文件最后上传的区块
        if (type == 0) {
            String scxUploadPath = ScxConfig.uploadFilePath + "TEMP\\" + fileName + "\\" + ".scxUpload";
            try {
                FileReader fileReader = new FileReader(scxUploadPath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                var lastChunk = bufferedReader.readLine().split("-")[0];
                fileReader.close();
                bufferedReader.close();
                return Json.ok().data("lastChunk", lastChunk);
            } catch (IOException e) {
                return Json.ok().data("lastChunk", 0);
            }
        } else {
            String fileWritePath = FileUtils.getDateStr() + '\\' + fileName;
            var b = FileUtils.validateFile(fileName, fileWritePath);
            if (b) {
                //保存数据
                UploadFile u = new UploadFile();
                u.fileName = fileName;
                u.filePath = fileWritePath;
                u.uploadTime = LocalDateTime.now();
                u.fileSize = fileSize;
                u.id = uploadFileService.save(u).id;
                return Json.ok().items(u);
            } else {
                return Json.fail("上传失败");
            }
        }
    }
}
