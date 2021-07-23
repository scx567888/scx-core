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
     * 模块根路径
     * 如果模块是 jar 就获取 jar 所在目录
     * 如果 模块不是 jar 就获取 所在 class 的目录
     */
    public final File moduleRootPath;

    /**
     * 根据 baseModule 创建 ScxModule
     *
     * @param baseModule b
     * @param <T>        t
     * @throws java.net.URISyntaxException if any.
     * @throws java.io.IOException         if any.
     */
    public <T extends BaseModule> ScxModule(T baseModule) throws URISyntaxException, IOException {
        var moduleClass = baseModule.getClass();
        this.basePackage = moduleClass.getPackageName();
        this.moduleName = moduleClass.getSimpleName();
        this.baseModuleExample = baseModule;
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
