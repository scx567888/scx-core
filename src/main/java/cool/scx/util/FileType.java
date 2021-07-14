package cool.scx.util;

import cool.scx.enumeration.MediaType;

/**
 * 文件类型 存储常用文件的基本信息 主要用途是作为一个索引表对文件进行判断
 *
 * @author scx567888
 * @version 1.0.10
 */
public class FileType {

    /**
     * 文件后缀
     */
    public final String fileExtension;

    /**
     * 对应的 mimeType
     */
    public final String mimeType;

    /**
     * 媒体类型
     */
    public final MediaType mediaType;

    /**
     * 文件头
     */
    public final String fileHeader;

    /**
     * 文件说明
     */
    public final String description;

    public FileType(String fileExtension, String mimeType, MediaType mediaType, String fileHeader, String description) {
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
        this.mediaType = mediaType;
        this.fileHeader = fileHeader;
        this.description = description;
    }

}
