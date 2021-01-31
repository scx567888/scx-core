package cool.scx.base;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.business.system.ScxLogService;
import cool.scx.business.uploadfile.UploadFile;
import cool.scx.business.uploadfile.UploadFileService;
import cool.scx.business.user.UserService;
import cool.scx.enumeration.HttpMethod;
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
    private final UserService userService;
    //
//    /**
//     * 根据实体条件查询实体列表带 Like 条件 需要在实体类上注解@Like
//     * 查询分页数据（提供模糊查询）
//     *
//     * @param param e
//     * @return e
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public List<Entity> list(Param<Entity> param) {
//        return baseDao.list(param);
//    }
//
//    @ScxMapping(useMethodNameAsUrl = true)
//    public List<Map<String, Object>> listMapAll() {
//        return baseDao.listMapAll();
//    }
//
//    @ScxMapping(useMethodNameAsUrl = true)
//    public List<Map<String, Object>> listMap(Param<Entity> param) {
//        return baseDao.listMap(param);
//    }
//
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Integer count(Param<Entity> param) {
//        return baseDao.count(param, false);
//    }
//
//    /**
//     * 根据条件统计实体数 不提供模糊查询
//     *
//     * @param param e
//     * @return e
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Integer countIgnoreLike(Param<Entity> param) {
//        return baseDao.count(param, true);
//    }
//
//    /**
//     * 根据 field 获取 list 集合
//     *
//     * @param fieldName 字段名称
//     * @return 以 value 为键值的 list 集合
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public List<Map<String, Object>> getFieldList(String fieldName) {
//        return baseDao.getFieldList(fieldName);
//    }
//
//    /**
//     * 根据条件获取单个对象
//     *
//     * @param param a
//     * @return e
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Entity get(Param<Entity> param) {
//        return baseDao.get(param);
//    }
    private final UploadFileService uploadFileService;

    public BaseController(ScxLogService scxLogService, UserService userService, UploadFileService uploadFileService) {
        this.scxLogService = scxLogService;
        this.userService = userService;
        this.uploadFileService = uploadFileService;
    }

    //    /**
//     * 批量保存实体 (适用于少量数据 数据量 < 5000)
//     *
//     * @param entityList 实体集合
//     * @return 插入成功的数据 自增主键
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public List<Long> saveList(List<Entity> entityList) {
//        return baseDao.saveList(entityList);
//    }
//
//    /**
//     * 删除指定id的实体
//     *
//     * @param ids 要删除的 id 集合
//     * @return 被删除的数据条数 用于前台分页优化
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Integer deleteByIds(Long... ids) {
//        return baseDao.deleteByIds(ids);
//    }
//
//    /**
//     * 根据条件删除
//     *
//     * @param param e
//     * @return e
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Integer delete(Param<Entity> param) {
//        return baseDao.delete(param);
//    }
//
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Entity update(Param<Entity> param) {
//        return baseDao.update(param, false);
//    }
//
//    /**
//     * 根据主键查询
//     *
//     * @param id e
//     * @return e
//     */
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Entity getById(Long id) {
//        return baseDao.getById(id);
//    }
//
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Entity updateById(Entity entity) {
//        return baseDao.update(new Param<>(entity), false);
//    }
//
//    @ScxMapping(useMethodNameAsUrl = true)
//    public Entity updateIncludeNull(Param<Entity> param) {
//        return baseDao.update(param, true);
//    }
//
    @ScxMapping(value = ":modelName/list", httpMethod = HttpMethod.GET)
    public Json listAll(String modelName, Map<String, Object> objectMap) {
        System.out.println();
        modelName = modelName.toLowerCase();
        var modelClass = ScxContext.getBaseModelClassByName(modelName);
        var baseServiceByName = (BaseService<?>) ScxContext.getBaseServiceByName(modelName + "service");
        var objects = baseServiceByName.listMapAll();
        return Json.ok().tables(objects, objects.size());
    }

    /**
     * 实体插入新对象,并返回插入的实体
     *
     * @param modelName model 的名称
     * @param entityMap 前台传过来的 map 以键值对的形式表示的 实体类
     * @return 实体
     */
    @ScxMapping(value = ":modelName/save", httpMethod = HttpMethod.POST)
    @SuppressWarnings("unchecked")
    public Json save(String modelName, Map<String, Object> entityMap) {
        if (entityMap != null) {
            modelName = modelName.toLowerCase();
            var baseService = (BaseService<BaseModel>) ScxContext.getBaseServiceByName(modelName + "service");
            var realObject = (BaseModel) ObjectUtils.mapToBean(entityMap, ScxContext.getBaseModelClassByName(modelName));
            var newObjectId = baseService.save(realObject);
            var newObject = baseService.getById(newObjectId);
            return Json.ok().items(newObject);
        }
        return Json.fail("参数为空");
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
                u.id = uploadFileService.save(u);
                return Json.ok().items(u);
            } else {
                return Json.fail("上传失败");
            }
        }
    }
}
