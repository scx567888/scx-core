package cool.scx.util;

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
     * 此种文件类型 mimeType
     */
    public final String mimeType;

    /**
     * 文件头
     */
    public final String fileHeader;

    /**
     * 文件说明
     */
    public final String description;


    /**
     * 是否为图片
     */
    public final boolean isImage;

    /**
     * <p>Constructor for FileType.</p>
     *
     * @param fileHeader    a {@link java.lang.String} object.
     * @param fileExtension a {@link java.lang.String} object.
     * @param mimeType      a {@link java.lang.String} object.
     * @param description   a {@link java.lang.String} object.
     * @param isImage       a boolean
     */
    public FileType(String fileExtension, String mimeType, String fileHeader, String description, boolean isImage) {
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
        this.fileHeader = fileHeader;
        this.description = description;
        this.isImage = isImage;
    }
}
