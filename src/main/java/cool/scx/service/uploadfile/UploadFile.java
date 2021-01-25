package cool.scx.service.uploadfile;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.Date;

/**
 * 文件上传表
 */
@ScxModel(tablePrefix = "core")
public class UploadFile extends BaseModel {

    public String filePath;//文件存储的路径 (相对与上传根目录的)

    public String fileSize;//文件的大小 (格式化后的)

    public String fileName;//文件名

    public Date uploadTime;//上传日期

}
