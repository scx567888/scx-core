package cool.scx.business.uploadfile;

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

    public String filePath;//文件存储的路径 (相对与上传根目录的)

    public String fileSizeDisplay;//文件的大小 (格式化后的 就是人能看懂的那种)

    public Long fileSize;//文件的大小

    public String fileName;//文件名

    public LocalDateTime uploadTime;//上传日期

    public String fileMD5;//文件的 md5 值

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

}
