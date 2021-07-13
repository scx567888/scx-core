package cool.scx.module;

import cool.scx.ScxModule;

import java.io.File;
import java.util.List;

/**
 * 模块实体 , 注意和 ScxModule 接口区分开
 *
 * @author scx567888
 * @version 1.1.2
 */
public class ScxModuleItem {

    /**
     * 模块名称
     */
    public String moduleName;

    /**
     * 模块的 class
     */
    public Class<? extends ScxModule> moduleClass;

    /**
     * 模块中所有的 class
     */
    public List<Class<?>> classList;

    /**
     * 模块的基本包 需要根据此包进行扫描
     */
    public String basePackage;

    /**
     * 模块根路径
     * 如果模块是 jar 就获取 jar 所在目录
     * 如果 模块不是 jar 就获取 所在 class 的目录
     */
    public File moduleRootPath;

}
