package cool.scx.module;

import cool.scx.BaseModule;
import cool.scx.util.ScanClassUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

/**
 * ScxModule 用于承载业务模块
 *
 * @author scx567888
 * @version 1.1.2
 */
public class ScxModule implements Serializable {

    /**
     * 模块名称 用于区分模块 (不允许重复)
     */
    public final String moduleName;

    /**
     * 需要扫描类的 basePackage
     */
    public final String basePackage;

    /**
     * baseModule 实例 用于执行生命周期
     */
    public final BaseModule baseModuleExample;

    /**
     * 模块中所有的 class
     */
    public final List<Class<?>> classList;

    /**
     * 是否为插件
     */
    public final boolean isPlugin;

    /**
     * 模块根路径
     * 如果模块是 jar 就获取 jar 所在目录
     * 如果 模块不是 jar 就获取 所在 class 的目录
     */
    public final File moduleRootPath;

    /**
     * 根据 jar 文件创建 ScxModule
     *
     * @param jarFile jar文件
     */
    public ScxModule(File jarFile, boolean isPlugin) throws IOException {
        //判断文件是否为 jar 包,不是则抛出异常
        if (!ScanClassUtils.isJar(jarFile)) {
            throw new IllegalArgumentException();
        }
        this.basePackage = "";
        this.moduleName = jarFile.getName();
        this.baseModuleExample = null;
        this.isPlugin = isPlugin;
        this.classList = ScanClassUtils.getClassListByJar(jarFile.toURI());
        this.moduleRootPath = jarFile.getParentFile();
    }

    /**
     * 根据 baseModule 创建 ScxModule
     *
     * @param baseModule b
     * @param <T>        t
     */
    public <T extends BaseModule> ScxModule(T baseModule) throws URISyntaxException, IOException {
        var moduleClass = baseModule.getClass();
        this.basePackage = moduleClass.getPackageName();
        this.moduleName = moduleClass.getSimpleName();
        this.baseModuleExample = baseModule;
        this.isPlugin = false;
        var classSource = ScanClassUtils.getClassSource(moduleClass);
        var classSourceFile = new File(classSource);
        //判断当前是否处于 jar 包中
        if (ScanClassUtils.isJar(classSourceFile)) {
            var allClassList = ScanClassUtils.getClassListByJar(classSource);
            this.classList = ScanClassUtils.filterByBasePackage(allClassList, basePackage);
            this.moduleRootPath = classSourceFile.getParentFile();
        } else {
            var allClassList = ScanClassUtils.getClassListByDir(classSource, moduleClass.getClassLoader());
            this.classList = ScanClassUtils.filterByBasePackage(allClassList, basePackage);
            this.moduleRootPath = classSourceFile;
        }
    }

}
