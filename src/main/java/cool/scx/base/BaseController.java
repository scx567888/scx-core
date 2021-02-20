package cool.scx.base;

import cool.scx.annotation.PathParam;
import cool.scx.annotation.QueryParam;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.boot.ScxConfig;
import cool.scx.boot.ScxContext;
import cool.scx.business.system.ScxLogService;
import cool.scx.business.uploadfile.UploadFile;
import cool.scx.business.uploadfile.UploadFileService;
import cool.scx.enumeration.HttpMethod;
import cool.scx.exception.HttpResponseException;
import cool.scx.util.*;
import cool.scx.vo.Binary;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>BaseController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController("api")
public class BaseController {

    private final ScxLogService scxLogService;

    private final UploadFileService uploadFileService;

    /**
     * <p>Constructor for BaseController.</p>
     *
     * @param scxLogService     a {@link cool.scx.business.system.ScxLogService} object.
     * @param uploadFileService a {@link cool.scx.business.uploadfile.UploadFileService} object.
     */
    public BaseController(ScxLogService scxLogService, UploadFileService uploadFileService) {
        this.scxLogService = scxLogService;
        this.uploadFileService = uploadFileService;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseModel> Param<T> getParam(String modelName, Map<String, Object> params) {
        var modelClass = (Class<T>) ScxContext.getClassByName(modelName);
        Param<T> p = new Param<>(ObjectUtils.mapToBean(params, modelClass));
        Integer page = (Integer) params.get("page");
        Integer limit = (Integer) params.get("limit");
        p.setPagination(page, limit);
        return p;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseModel> BaseService<T> getBaseService(String modelName) {
        try {
            var o = ScxContext.getBean(ScxContext.getClassByName(modelName.toLowerCase() + "service"));
            return (BaseService<T>) o;
        } catch (Exception e) {
            throw new RuntimeException(modelName.toLowerCase() + "service : 不存在!!!");
        }
    }

    //

    /**
     * <p>list.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param params    a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/list", httpMethod = {HttpMethod.GET, HttpMethod.POST})
    public Json list(String modelName, Map<String, Object> params) {
        if (params == null) {
            return Json.fail("查询参数不能为空");
        }
        var baseService = getBaseService(modelName);
        var param = getParam(modelName, params);
        var list = baseService.list(param);
        var count = baseService.count(param);
        return Json.ok().tables(list, count);
    }

    /**
     * <p>info.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Long} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/:id", httpMethod = HttpMethod.GET)
    public Json info(String modelName, Long id) {
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
     */
    @ScxMapping(value = ":modelName", httpMethod = HttpMethod.POST)
    public Json save(String modelName, Map<String, Object> entityMap) {
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
    @ScxMapping(value = ":modelName", httpMethod = HttpMethod.PUT)
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
    @ScxMapping(value = ":modelName/:id", httpMethod = HttpMethod.DELETE)
    public Json delete(String modelName, Integer id) throws Exception {
        var baseService = getBaseService(modelName);
        var deleteByIds = baseService.deleteByIds(Long.valueOf(id));
        return Json.ok().items(deleteByIds == 1);
    }

    /**
     * <p>batchDelete.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param params    a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/batchDelete", httpMethod = HttpMethod.DELETE)
    public Json batchDelete(String modelName, Map<String, Object> params) {
        var deleteIds = (Long[]) params.get("deleteIds");
        var baseService = getBaseService(modelName);
        var deletedCount = baseService.deleteByIds(deleteIds);
        return Json.ok("success").data("deletedCount", deletedCount);
    }

    /**
     * <p>revokeDelete.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/revokeDelete/:id", httpMethod = HttpMethod.GET)
    public Json revokeDelete(String modelName, Integer id) {
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
     */
    @ScxMapping(value = ":modelName/getAutoComplete/:fieldName", httpMethod = HttpMethod.POST)
    public Json getAutoComplete(String modelName, String fieldName) {
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
     */
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
     * @param year      year
     * @param month     month
     * @param day       day
     * @param hour      hour
     * @param timestamp timestamp
     * @param fileName  要下载的文件名
     */
    @ScxMapping("/download/:year/:month/:day/:hour/:timestamp/:fileName")
    public void download(String year, String month, String day, String hour, String timestamp, String fileName) {
        var fullPath = ScxConfig.uploadFilePath + year + "/" + month + "/" + day + "/" + hour + "/" + timestamp + "/" + fileName;
        //FileUtils.downloadFile(response, request, fullPath);
        scxLogService.outAndRecordLog("ip 为 :" + NetUtils.getIpAddr() + "的用户 下载了" + fileName);
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
     */
    @ScxMapping(value = "/showPicture/:year/:month/:day/:hour/:timestamp/:fileName", httpMethod = HttpMethod.GET, unCheckedLogin = true)
    public Binary showPicture(String year,
                              String month,
                              String day,
                              String hour,
                              String timestamp,
                              String fileName,
                              @QueryParam("w") Integer width,
                              @QueryParam("h") Integer height, RoutingContext ctx) throws HttpResponseException {

        var filePath = ScxConfig.uploadFilePath + "/" + year + "/" + month + "/" + day + "/" + hour + "/" + timestamp + "/" + fileName;
        var file = new File(filePath);

        if (!file.exists()) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404));
        }
        //设置缓存 减少服务器压力
        ctx.response().putHeader("Cache-Control", "max-age=2628000");
        FileType imageFileType = FileTypeUtils.getImageFileType(file);
        try {
            var out = new ByteArrayOutputStream();
            if (imageFileType == null) {
                var image = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
                var myImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                var g = myImage.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                ImageIO.write(myImage, "png", out);
                return new Binary(out.toByteArray());
            } else if (height == null && width == null) {
                return new Binary(file);
            } else {
                var image = Thumbnails.of(file).scale(1.0).asBufferedImage();
                if (height == null || height > image.getHeight()) {
                    height = image.getHeight();
                }
                if (width == null || width > image.getWidth()) {
                    width = image.getWidth();
                }
                Thumbnails.of(file).size(width, height).keepAspectRatio(false).toOutputStream(out);
                return new Binary(out.toByteArray());
            }
        } catch (Exception e) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404));
        }

    }


    /**
     * 通用查看图片方法
     *
     * @param id 要显示的图片 id
     *           下载文件或错误
     */
    @ScxMapping("/showPictureById/:id")
    public Binary showPictureById(@PathParam Long id, @QueryParam("w") Integer width,
                                  @QueryParam("h") Integer height, RoutingContext ctx) throws HttpResponseException {
        var uploadFile = uploadFileService.getById(id);
        var file = new File(ScxConfig.uploadFilePath + "/" + uploadFile.filePath);

        if (!file.exists()) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404));
        }
        //设置缓存 减少服务器压力
        ctx.response().putHeader("Cache-Control", "max-age=2628000");
        FileType imageFileType = FileTypeUtils.getImageFileType(file);
        try {
            var out = new ByteArrayOutputStream();
            if (imageFileType == null) {
                var image = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
                var myImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                var g = myImage.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                ImageIO.write(myImage, "png", out);
                return new Binary(out.toByteArray());
            } else if (height == null && width == null) {
                return new Binary(file);
            } else {
                var image = Thumbnails.of(file).scale(1.0).asBufferedImage();
                if (height == null || height > image.getHeight()) {
                    height = image.getHeight();
                }
                if (width == null || width > image.getWidth()) {
                    width = image.getWidth();
                }
                Thumbnails.of(file).size(width, height).keepAspectRatio(false).toOutputStream(out);
                return new Binary(out.toByteArray());
            }
        } catch (Exception e) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404));
        }
    }


    /**
     *
     *
     * @param file
     * @param fileName
     * @return
     */


    /**
     * 单个文件上传 和 分片文件上传
     *
     * @param file         文件
     * @param fileName     文件名
     * @param fileSize     文件大小
     * @param chunksNumber 当前分片数
     * @param chunk        总分片数
     * @param type         文件类型 , 分为 单个文件和分片文件
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
