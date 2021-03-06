package cool.scx._module.base;

import cool.scx.annotation.FromPath;
import cool.scx.annotation.FromQuery;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.FileUpload;
import cool.scx.bo.Query;
import cool.scx.enumeration.Method;
import cool.scx.enumeration.WhereType;
import cool.scx.exception.HttpRequestException;
import cool.scx.exception.NotFoundException;
import cool.scx.util.*;
import cool.scx.vo.Binary;
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

/**
 * <p>UploadController class.</p>
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api")
public class BaseController {

    private final UploadFileService uploadFileService;

    /**
     * 构造函数
     *
     * @param uploadFileService u
     */
    public BaseController(UploadFileService uploadFileService) {
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
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = "/download/:fileId", method = {Method.GET, Method.HEAD})
    public Download download(String fileId) throws HttpRequestException {
        var param = new Query().addWhere("fileId", WhereType.EQUAL, fileId);
        UploadFile uploadFile = uploadFileService.get(param);
        if (uploadFile == null) {
            throw new NotFoundException();
        }
        var file = new File(BaseConfig.uploadFilePath(), uploadFile.filePath);
        if (!file.exists()) {
            throw new NotFoundException();
        }
        Ansi.OUT.brightBlue("ip 为 :" + NetUtils.getIpAddr() + "的用户 下载了" + uploadFile.fileName).ln();
        //  这里让文件限速到 500 kb
        return new Download(file, uploadFile.fileName);
    }

    /**
     * 直接展示文件方法
     *
     * @param fileId a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Download} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = "/binary/:fileId", method = {Method.GET, Method.HEAD})
    public Binary binary(String fileId) throws HttpRequestException {
        var param = new Query().addWhere("fileId", WhereType.EQUAL, fileId);
        UploadFile uploadFile = uploadFileService.get(param);
        if (uploadFile == null) {
            throw new NotFoundException();
        }
        var file = new File(BaseConfig.uploadFilePath(), uploadFile.filePath);
        if (!file.exists()) {
            throw new NotFoundException();
        }
        return new Binary(file);
    }

    /**
     * 通用查看图片方法
     *
     * @param fileId 文件 id
     * @param width  a {@link java.lang.Integer} object.
     * @param height a {@link java.lang.Integer} object.
     * @param type   a {@link java.lang.String} object
     * @return a {@link cool.scx.vo.Binary} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     */
    @ScxMapping(value = "/picture/:fileId", method = {Method.GET, Method.HEAD})
    public Image picture(@FromPath String fileId,
                         @FromQuery(value = "w", required = false) Integer width,
                         @FromQuery(value = "h", required = false) Integer height,
                         @FromQuery(value = "t", required = false) String type) throws HttpRequestException {
        var param = new Query().addWhere("fileId", WhereType.EQUAL, fileId);
        UploadFile uploadFile = uploadFileService.get(param);
        if (uploadFile == null) {
            throw new NotFoundException();
        } else {
            return new Image(new File(BaseConfig.uploadFilePath(), uploadFile.filePath), width, height, type);
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
        var uploadTempFile = BaseConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "_" + fileName + "\\.scxTemp";
        var uploadConfigFile = new File(BaseConfig.uploadFilePath().getPath() + "\\TEMP\\" + fileMD5 + "_" + fileName + "\\.scxUpload");
        //先判断 文件是否已经上传过
        if (StringUtils.isNotEmpty(fileMD5)) {
            UploadFile fileByMd5 = uploadFileService.findFileByMd5(fileMD5);
            //证明有其他人上传过此文件 就不上传了 直接 返回文件上传成功的信息给用户
            if (fileByMd5 != null) {
                File file = new File(BaseConfig.uploadFilePath() + "\\" + fileByMd5.filePath);
                if (file.exists()) {
                    var save = uploadFileService.save(copyUploadFile(fileName, fileByMd5));
                    //有可能有之前的残留临时文件 再次一并清楚
                    FileUtils.deleteFiles(Path.of(uploadTempFile).getParent());
                    return Json.ok().put("type", "alreadyExists").put("items", save);
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
            var fileStoragePath = BaseConfig.uploadFilePath().getPath() + "\\" + uploadFile.filePath;
            //计算 md5 只有前后台 md5 相同文件才算 正确
            var serverMd5Str = DigestUtils.md5(new File(uploadTempFile));
            if (!fileMD5.equalsIgnoreCase(serverMd5Str)) {
                //md5 不相同 说明临时文件可能损坏 删除临时文件
                FileUtils.deleteFiles(Path.of(uploadTempFile).getParent());
                return Json.ok().put("type", "uploadFail");
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
                return Json.ok().put("type", "uploadSuccess").put("items", save);
            } else {
                //移动失败 返回上传失败的信息
                return Json.ok().put("type", "uploadFail");
            }
        } else {
            var lastUploadChunk = getLastUploadChunk(uploadConfigFile, chunkLength);
            if (nowChunkIndex - lastUploadChunk == 1) {
                FileUtils.fileAppend(uploadTempFile, fileData.buffer.getBytes());
                changeLastUploadChunk(uploadConfigFile, nowChunkIndex, chunkLength);
                return Json.ok().put("type", "needMore").put("items", nowChunkIndex);
            } else {
                return Json.ok().put("type", "needMore").put("items", lastUploadChunk);
            }
        }
    }

    /**
     * <p>listFile.</p>
     *
     * @param fileIds a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = "/upload-file/list-file", method = Method.POST)
    public Json listFile(List<String> fileIds) {
        var param = new Query();
        if (StringUtils.isNotEmpty(fileIds)) {
            param.addWhere("fileId", WhereType.IN, fileIds);
        } else {
            param.addWhere("fileId", WhereType.EQUAL, -1);
        }
        return Json.ok().put("items", uploadFileService.list(param));
    }

    /**
     * <p>deleteFile.</p>
     *
     * @param fileId a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = "/upload-file/delete-file", method = Method.DELETE)
    public Json deleteFile(String fileId) {
        //先获取文件的基本信息
        var param = new Query().addWhere("fileId", WhereType.EQUAL, fileId);
        var needDeleteFile = uploadFileService.get(param);
        if (needDeleteFile != null) {
            //判断文件是否被其他人引用过
            var param1 = new Query().addWhere("fileMD5", WhereType.EQUAL, needDeleteFile.fileMD5);
            long count = uploadFileService.count(param1);

            //没有被其他人引用过 可以删除物理文件
            if (count == 1) {
                var filePath = BaseConfig.uploadFilePath() + "\\" + needDeleteFile.filePath;
                var file = new File(filePath);
                if (file.exists()) {
                    boolean b = FileUtils.deleteFiles(Path.of(BaseConfig.uploadFilePath() + "\\" + needDeleteFile.filePath).getParent());
                    if (!b) {
                        return Json.fail();
                    }
                }
            }
            //删除数据库中的文件数据
            uploadFileService.delete(needDeleteFile.id);
        }

        return Json.ok();
    }

}
