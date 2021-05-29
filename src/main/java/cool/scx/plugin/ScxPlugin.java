package cool.scx.plugin;

import cool.scx.config.ScxConfig;
import cool.scx.module.ModuleItem;
import cool.scx.module.ScxModule;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;

import java.io.File;

/**
 * ScxPlugins 插件
 *
 * @author 司昌旭
 * @version 1.1.0
 */
public final class ScxPlugin {

    /**
     * Constant <code>PLUGIN_ROOT</code>
     */
    public static final File PLUGIN_ROOT = FileUtils.getFileByRootModulePath("plugins");

    /**
     * <p>reloadPlugins.</p>
     */
    public static void reloadPlugins() {
        Ansi.OUT.yellow("ScxPlugins 重新加载中...").ln();
        loadPlugins();
        Ansi.OUT.yellow("ScxPlugins 重新加载完成...").ln();
    }

    /**
     * 初始化插件
     */
    public static void initPlugins() {
        Ansi.OUT.yellow("ScxPlugins 初始化中...").ln();
        loadPlugins();
        Ansi.OUT.yellow("ScxPlugins 初始化完成...").ln();
    }

    /**
     * <p>init.</p>
     */
    private static void loadPlugins() {
        if (PLUGIN_ROOT.exists()) {
            var allPluginJar = PLUGIN_ROOT.listFiles((file, s) -> s.endsWith(".jar"));
            if (allPluginJar != null) {
                for (File file : allPluginJar) {
                    //判断是否被禁用
                    var f = ScxConfig.disabledPluginList().contains(file.getName());
                    if (f) {
                        Ansi.OUT.brightRed("找到插件 名称 [" + file.getName() + "] 已禁用!!!").ln();
                    } else {
                        try {
                            ModuleItem moduleByFile = ScxModule.getModuleByFile(file);
                            moduleByFile.isPlugin = true;
                            ScxModule.addModule(moduleByFile);
                            Ansi.OUT.yellow("找到插件 文件名称 [" + file.getName() + "] 插件名称 [" + moduleByFile.moduleName + "] 已加载!!!").ln();
                        } catch (Exception e) {
                            Ansi.OUT.red("找到插件 文件名称 [" + file.getName() + "] 已损坏!!!").ln();
                        }
                    }
                }
            }
            Ansi.OUT.yellow("共加载 " + ScxModule.getAllPluginModule().size() + " 个插件!!!").ln();
        } else {
            Ansi.OUT.red("插件目录不存在 未加载任何插件!!!").ln();
        }
    }
}
