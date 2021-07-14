package cool.scx._ext.upload;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.time.LocalDateTime;

/**
 * 文件上传表
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "base")
public class UploadFile extends BaseModel {


    /**
     * 这里为了防止用户可以根据 id 猜测出来文件 业务中不使用 BaseModel 的 id
     */
    public String fileId;

    /**
     * 文件存储的路径 (相对与上传根目录的)
     */
    @Column(type = "TEXT")
    public String filePath;

    /**
     * 文件的大小 (格式化后的 就是人能看懂的那种)
     */
    public String fileSizeDisplay;

    /**
     * 文件的大小 long
     */
    public Long fileSize;

    /**
     * 原始文件名
     */
    @Column(type = "TEXT", notNull = true)
    public String fileName;

    /**
     * 上传日期
     */
    public LocalDateTime uploadTime;

    /**
     * 文件的 md5 值
     */
    @Column(needIndex = true, excludeOnUpdate = true, notNull = true)
    public String fileMD5;

}
