package cool.scx.boot;

import cool.scx.base.BaseModule;

import java.io.File;
import java.util.List;

/**
 * 模块实体
 */
public class ScxModule {

    /**
     * 模块名称
     */
    public String moduleName;

    /**
     * 模块的 class
     */
    public Class<? extends BaseModule> moduleClass;

    /**
     * 模块中所有的 class
     */
    public List<Class<?>> classList;

    /**
     * 模块的基本包 需要根据此包进行扫描
     */
    public String basePackage;

    /**
     * 是否为插件 (从 plugin 目录加载的)
     */
    public Boolean isPlugin;

    /**
     * 是否为根模块
     */
    public Boolean isRootModule;

    /**
     * 模块根路径
     * 如果模块是 jar 就获取 jar 所在目录
     * 如果 模块不是 jar 就获取 所在 class 的目录
     */
    public File moduleRootPath;

}
