package cool.scx.core.uploadfile;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;
import cool.scx.util.StringUtils;
import cool.scx.util.file.FileUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件上传表
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class UploadFile extends BaseModel {

    public String fileId;//这里为了防止用户可以根据 id 猜测出来文件 不使用 basemodel 的 id

    @Column(type = "TEXT")
    public String filePath;//文件存储的路径 (相对与上传根目录的)

    public String fileSizeDisplay;//文件的大小 (格式化后的 就是人能看懂的那种)

    public Long fileSize;//文件的大小

    @Column(type = "TEXT", notNull = true)
    public String fileName;//文件名

    public LocalDateTime uploadTime;//上传日期

    @Column(needIndex = true, excludeOnUpdate = true, notNull = true)
    public String fileMD5;//文件的 md5 值

    /**
     * <p>getNewUpload.</p>
     *
     * @param fileName a {@link java.lang.String} object.
     * @param fileSize a {@link java.lang.Long} object.
     * @param fileMD5  a {@link java.lang.String} object.
     * @return a {@link cool.scx.core.uploadfile.UploadFile} object.
     */
    public static UploadFile getNewUpload(String fileName, Long fileSize, String fileMD5) {
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

    //复制一个 uploadFile 但是 id 不相同

    /**
     * <p>copyUploadFile.</p>
     *
     * @param fileName      a {@link java.lang.String} object.
     * @param oldUploadFile a {@link cool.scx.core.uploadfile.UploadFile} object.
     * @return a {@link cool.scx.core.uploadfile.UploadFile} object.
     */
    public static UploadFile copyUploadFile(String fileName, UploadFile oldUploadFile) {
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

}
