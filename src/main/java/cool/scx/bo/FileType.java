package cool.scx.bo;

/**
 * 文件类型 存储常用文件的基本信息 主要用途是作为一个索引表对文件进行判断
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class FileType {

    /**
     * 文件头
     */
    public final String head;

    /**
     * 文件后缀
     */
    public final String suffix;

    /**
     * 此种文件类型在 浏览器中默认对应的  contentType
     */
    public final String contentType;

    /**
     * 文件说明
     */
    public final String description;

    /**
     * <p>Constructor for FileType.</p>
     *
     * @param head        a {@link java.lang.String} object.
     * @param suffix      a {@link java.lang.String} object.
     * @param contentType a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     */
    public FileType(String head, String suffix, String contentType, String description) {
        this.head = head;
        this.suffix = suffix;
        this.contentType = contentType;
        this.description = description;
    }
}
