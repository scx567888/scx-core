package cool.scx.plugin;

import cool.scx.boot.ScxModule;
import cool.scx.boot.ScxModuleHandler;
import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;

import java.util.Arrays;

/**
 * ScxPlugins 插件
 *
 * @author 司昌旭
 * @version 1.1.0
 */
public final class ScxPlugins {

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
        var pluginsRoot = ScxConfig.pluginRoot();
        if (pluginsRoot.exists()) {
            Arrays.stream(pluginsRoot.listFiles()).filter(file -> file.getName().endsWith(".jar")).filter(file -> {
                var f = ScxConfig.pluginDisabledList().contains(file.getName());
                if (f) {
                    Ansi.OUT.brightRed("找到插件 名称 [" + file.getName() + "] 已禁用!!!").ln();
                }
                return !f;
            }).forEach(file -> {
                try {
                    ScxModule moduleByFile = ScxModuleHandler.getModuleByFile(file);
                    moduleByFile.isPlugin = true;
                    ScxModuleHandler.addModule(moduleByFile);
                    Ansi.OUT.yellow("找到插件 文件名称 [" + file.getName() + "] 插件名称 [" + moduleByFile.moduleName + "] 已加载!!!").ln();
                } catch (Exception e) {
                    Ansi.OUT.red("找到插件 文件名称 [" + file.getName() + "] 已损坏!!!").ln();
                }
            });
        }
    }
}
