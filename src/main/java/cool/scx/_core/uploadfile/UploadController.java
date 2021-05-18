package cool.scx._core.uploadfile;

import cool.scx.annotation.FromQuery;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.FileUpload;
import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.enumeration.Method;
import cool.scx.exception.HttpResponseException;
import cool.scx.util.LogUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.file.FileUtils;
import cool.scx.vo.Download;
import cool.scx.vo.Image;
import cool.scx.vo.Json;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>UploadController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController("/api")
public class UploadController {

    private final UploadFileService uploadFileService;

    /**
     * <p>Constructor for UploadController.</p>
     *
     * @param uploadFileService a {@link UploadFileService} object.
     */
    public UploadController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    /**
     * 通用下载资源方法
     *
     * @param fileId a {@link String} object.
     * @return a {@link Download} object.
     * @throws HttpResponseException if any.
     */
    @ScxMapping(value = "/download/:fileId", method = Method.GET)
    public Download download(String fileId) throws HttpResponseException {
        var param = new Param<>(new UploadFile());
        param.queryObject.fileId = fileId;
        UploadFile uploadFile = uploadFileService.get(param);
        if (uploadFile == null) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404).send("Not Found!!!"));
        }
        var file = new File(ScxConfig.uploadFilePath() + "\\" + uploadFile.filePath);
        if (!file.exists()) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404).send("Not Found!!!"));
        }
        LogUtils.recordLog("ip 为 :" + NetUtils.getIpAddr() + "的用户 下载了" + uploadFile.fileName);
        //  这里让文件限速到 500 kb
        return new Download(file, uploadFile.fileName);
    }

    /**
     * 通用查看图片方法
     *
     * @param fileId 文件 id
     * @param width  a {@link Integer} object.
     * @param height a {@link Integer} object.
     * @return a {@link cool.scx.vo.Binary} object.
     * @throws HttpResponseException if any.
     */
    @ScxMapping(value = "/showPicture/:fileId", method = Method.GET)
    public Image showPicture(String fileId, @FromQuery("w") Integer width, @FromQuery("h") Integer height) throws HttpResponseException {
        var param = new Param<>(new UploadFile());
        param.queryObject.fileId = fileId;
        UploadFile uploadFile = uploadFileService.get(param);
        if (uploadFile == null) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404).send("Not Found!!!"));
        } else {
            return new Image(new File(ScxConfig.uploadFilePath() + "\\" + uploadFile.filePath), width, height);
        }

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
        var uploadTempFile = ScxConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "_" + fileName + "\\.scxTemp";
        var uploadConfigFile = new File(ScxConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "_" + fileName + "\\.scxUpload");
        //先判断 文件是否已经上传过
        if (StringUtils.isNotEmpty(fileMD5)) {
            UploadFile fileByMd5 = uploadFileService.findFileByMd5(fileMD5);
            //证明有其他人上传过此文件 就不上传了 直接 返回文件上传成功的信息给用户
            if (fileByMd5 != null) {
                File file = new File(ScxConfig.uploadFilePath() + "\\" + fileByMd5.filePath);
                if (file.exists()) {
                    var save = uploadFileService.save(UploadFile.copyUploadFile(fileName, fileByMd5));
                    //有可能有之前的残留临时文件 再次一并清楚
                    FileUtils.deleteFiles(Path.of(uploadTempFile).getParent());
                    return Json.ok().data("type", "alreadyExists").items(save);
                }
            }
        }

        //最后一个分块 上传完成
        if (nowChunkIndex.equals(chunkLength)) {
            //先将数据写入临时文件中
            FileUtils.fileAppend(uploadTempFile, fileData.buffer.getBytes());
            //获取文件信息描述对象
            var uploadFile = UploadFile.getNewUpload(fileName, fileSize, fileMD5);
            //获取文件真实的存储路径
            var fileStoragePath = ScxConfig.uploadFilePath().getPath() + "\\" + uploadFile.filePath;
            //计算 md5 只有前后台 md5 相同文件才算 正确
            var serverMd5Str = FileUtils.fileMD5(uploadTempFile);
            if (!fileMD5.equalsIgnoreCase(serverMd5Str)) {
                //md5 不相同 说明临时文件可能损坏 删除临时文件
                FileUtils.deleteFiles(Path.of(uploadTempFile).getParent());
                return Json.ok().data("type", "uploadFail");
            }
            //讲临时文件移动并重命名到 真实的存储路径
            var renameSuccess = FileUtils.fileMove(uploadTempFile, fileStoragePath);
            //移动成功 说明文件上传成功
            if (renameSuccess) {
                //删除临时文件夹
                FileUtils.deleteFiles(Path.of(uploadTempFile).getParent());
                //存储到数据库
                var save = uploadFileService.save(uploadFile);
                //像前台发送上传成功的消息
                return Json.ok().data("type", "uploadSuccess").items(save);
            } else {
                //移动失败 返回上传失败的信息
                return Json.ok().data("type", "uploadFail");
            }
        } else {
            var lastUploadChunk = FileUtils.getLastUploadChunk(uploadConfigFile, chunkLength);
            if (nowChunkIndex - lastUploadChunk == 1) {
                FileUtils.fileAppend(uploadTempFile, fileData.buffer.getBytes());
                FileUtils.changeLastUploadChunk(uploadConfigFile, nowChunkIndex, chunkLength);
                return Json.ok().data("type", "needMore").items(nowChunkIndex);
            } else {
                return Json.ok().data("type", "needMore").items(lastUploadChunk);
            }
        }
    }


    /**
     * <p>listFile.</p>
     *
     * @param fileIds a {@link java.util.Map} object.
     * @return a {@link Json} object.
     */
    @ScxMapping(value = "/uploadFile/listFile", method = Method.POST)
    public Json listFile(String fileIds) {
        var param = new Param<>(new UploadFile());
        if (StringUtils.isNotEmpty(fileIds)) {
            String collect = Stream.of(fileIds.split(",")).map(s -> "'" + s + "'").collect(Collectors.joining(","));
            param.whereSql = " file_id in (" + collect + ")";
        } else {
            param.whereSql = " file_id = -1";
        }
        return Json.ok().items(uploadFileService.list(param));
    }


    /**
     * <p>deleteFile.</p>
     *
     * @param fileId a {@link String} object.
     * @return a {@link Json} object.
     */
    @ScxMapping(value = "/uploadFile/deleteFile", method = Method.DELETE)
    public Json deleteFile(String fileId) {
        //先获取文件的基本信息
        var param = new Param<>(new UploadFile());
        param.queryObject.fileId = fileId;
        UploadFile needDeleteFile = uploadFileService.get(param);

        if (needDeleteFile != null) {
            //判断文件是否被其他人引用过
            var param1 = new Param<>(new UploadFile());
            param1.queryObject.fileMD5 = needDeleteFile.fileMD5;
            Integer count = uploadFileService.count(param1);

            //没有被其他人引用过 可以删除物理文件
            if (count == 1) {
                var filePath = ScxConfig.uploadFilePath() + "\\" + needDeleteFile.filePath;
                var file = new File(filePath);
                if (file.exists()) {
                    boolean b = FileUtils.deleteFiles(Path.of(ScxConfig.uploadFilePath() + "\\" + needDeleteFile.filePath).getParent());
                    if (!b) {
                        return Json.ok("deleteFail");
                    }
                }
            }
            //删除数据库中的文件数据
            uploadFileService.deleteByIds(needDeleteFile.id);
        }

        return Json.ok("deleteSuccess");
    }


}
