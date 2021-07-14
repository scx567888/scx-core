package cool.scx._ext.cms;

/**
 * 描述一个文件(模板文件)的基本信息
 * 一般用于将物理文件的结构构建为树发送到前台进行查询
 *
 * @author scx567888
 * @version 1.0.10
 */
public class TemplateInfo {

    /**
     * 文件 ID
     */
    public String id;

    /**
     * 上级目录 ID
     */
    public String parentId;

    /**
     * 文件路径
     */
    public String filePath;

    /**
     * 文件类型 : 文件夹或文件
     */
    public String type;
}
