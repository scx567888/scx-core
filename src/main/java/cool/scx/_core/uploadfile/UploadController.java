package cool.scx._core.uploadfile;

import cool.scx._core.config.CoreConfig;
import cool.scx.annotation.FromQuery;
import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.FileUpload;
import cool.scx.bo.Param;
import cool.scx.enumeration.Method;
import cool.scx.exception.HttpResponseException;
import cool.scx.util.FileUtils;
import cool.scx.util.LogUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Download;
import cool.scx.vo.Image;
import cool.scx.vo.Json;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param uploadFileService a {@link cool.scx._core.uploadfile.UploadFileService} object.
     */
    public UploadController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    /**
     * <p>getLastUploadChunk.</p>
     *
     * @param uploadConfigFile a {@link File} object.
     * @param chunkLength      a {@link Integer} object.
     * @return a {@link Integer} object.
     */
    private static Integer getLastUploadChunk(File uploadConfigFile, Integer chunkLength) {
        try (var fr = new FileReader(uploadConfigFile); var br = new BufferedReader(fr)) {
            return Integer.parseInt(br.readLine().split("-")[0]);
        } catch (Exception e) {
            changeLastUploadChunk(uploadConfigFile, 0, chunkLength);
            return 0;
        }
    }

    /**
     * <p>changeLastUploadChunk.</p>
     *
     * @param uploadConfigFile a {@link File} object.
     * @param nowChunkIndex    a {@link Integer} object.
     * @param chunkLength      a {@link Integer} object.
     */
    private static void changeLastUploadChunk(File uploadConfigFile, Integer nowChunkIndex, Integer chunkLength) {
        try {
            Files.createDirectories(Path.of(uploadConfigFile.getParent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (var fw = new FileWriter(uploadConfigFile, false); var bw = new BufferedWriter(fw)) {
            bw.write(nowChunkIndex + "-" + chunkLength);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>getNewUpload.</p>
     *
     * @param fileName a {@link String} object.
     * @param fileSize a {@link Long} object.
     * @param fileMD5  a {@link String} object.
     * @return a {@link UploadFile} object.
     */
    private static UploadFile getNewUpload(String fileName, Long fileSize, String fileMD5) {
        var uploadFile = new UploadFile();
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy\\MM\\dd"));
        uploadFile.fileId = StringUtils.getUUID();
        uploadFile.fileName = fileName;
        uploadFile.uploadTime = LocalDateTime.now();
        uploadFile.fileSizeDisplay = FileUtils.longToDisplaySize(fileSize);
        uploadFile.fileSize = fileSize;
        uploadFile.fileMD5 = fileMD5;
        uploadFile.filePath = datePath + "\\" + uploadFile.fileId + "\\" + fileName;
        return uploadFile;
    }


    /**
     * <p>copyUploadFile.</p>
     *
     * @param fileName      a {@link String} object.
     * @param oldUploadFile a {@link UploadFile} object.
     * @return a {@link UploadFile} object.
     */
    private static UploadFile copyUploadFile(String fileName, UploadFile oldUploadFile) {
        var uploadFile = new UploadFile();
        uploadFile.fileId = StringUtils.getUUID();
        uploadFile.fileName = fileName;
        uploadFile.uploadTime = LocalDateTime.now();
        uploadFile.filePath = oldUploadFile.filePath;
        uploadFile.fileSizeDisplay = oldUploadFile.fileSizeDisplay;
        uploadFile.fileSize = oldUploadFile.fileSize;
        uploadFile.fileMD5 = oldUploadFile.fileMD5;
        return uploadFile;
    }

    /**
     * 通用下载资源方法
     * todo 优化性能
     *
     * @param fileId a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Download} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = "/download/:fileId", method = Method.GET)
    public Download download(String fileId) throws HttpResponseException {
        var param = new Param<>(new UploadFile());
        param.queryObject.fileId = fileId;
        UploadFile uploadFile = uploadFileService.get(param);
        if (uploadFile == null) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404).send("Not Found!!!"));
        }
        var file = new File(CoreConfig.uploadFilePath(), uploadFile.filePath);
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
     * @param width  a {@link java.lang.Integer} object.
     * @param height a {@link java.lang.Integer} object.
     * @param type   a {@link java.lang.String} object
     * @return a {@link cool.scx.vo.Binary} object.
     * @throws cool.scx.exception.HttpResponseException if any.
     */
    @ScxMapping(value = "/showPicture/:fileId", method = Method.GET)
    public Image showPicture(String fileId, @FromQuery("w") Integer width, @FromQuery("h") Integer height, @FromQuery("t") String type) throws HttpResponseException {
        var param = new Param<>(new UploadFile());
        param.queryObject.fileId = fileId;
        UploadFile uploadFile = uploadFileService.get(param);
        if (uploadFile == null) {
            throw new HttpResponseException(context -> context.response().setStatusCode(404).send("Not Found!!!"));
        } else {
            return new Image(new File(CoreConfig.uploadFilePath(), uploadFile.filePath), width, height, type);
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
        var uploadTempFile = CoreConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "_" + fileName + "\\.scxTemp";
        var uploadConfigFile = new File(CoreConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "_" + fileName + "\\.scxUpload");
        //先判断 文件是否已经上传过
        if (StringUtils.isNotEmpty(fileMD5)) {
            UploadFile fileByMd5 = uploadFileService.findFileByMd5(fileMD5);
            //证明有其他人上传过此文件 就不上传了 直接 返回文件上传成功的信息给用户
            if (fileByMd5 != null) {
                File file = new File(CoreConfig.uploadFilePath() + "\\" + fileByMd5.filePath);
                if (file.exists()) {
                    var save = uploadFileService.save(copyUploadFile(fileName, fileByMd5));
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
            var uploadFile = getNewUpload(fileName, fileSize, fileMD5);
            //获取文件真实的存储路径
            var fileStoragePath = CoreConfig.uploadFilePath().getPath() + "\\" + uploadFile.filePath;
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
            var lastUploadChunk = getLastUploadChunk(uploadConfigFile, chunkLength);
            if (nowChunkIndex - lastUploadChunk == 1) {
                FileUtils.fileAppend(uploadTempFile, fileData.buffer.getBytes());
                changeLastUploadChunk(uploadConfigFile, nowChunkIndex, chunkLength);
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
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = "/uploadFile/listFile", method = Method.POST)
    public Json listFile(List<String> fileIds) {
        var param = new Param<>(new UploadFile());
        if (StringUtils.isNotEmpty(fileIds)) {
            String collect = fileIds.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
            param.whereSql = " file_id in (" + collect + ")";
        } else {
            param.whereSql = " file_id = -1";
        }
        return Json.ok().items(uploadFileService.list(param));
    }

    /**
     * <p>deleteFile.</p>
     *
     * @param fileId a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
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
                var filePath = CoreConfig.uploadFilePath() + "\\" + needDeleteFile.filePath;
                var file = new File(filePath);
                if (file.exists()) {
                    boolean b = FileUtils.deleteFiles(Path.of(CoreConfig.uploadFilePath() + "\\" + needDeleteFile.filePath).getParent());
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
