package cool.scx._core.cms;

/**
 * 描述一个文件的基本信息
 * 一般用于将物理文件的结构构建为树发送到前台进行查询
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class FileInfo {
    public String id;
    public String parentId;
    public String filePath;
    public String type;
}
