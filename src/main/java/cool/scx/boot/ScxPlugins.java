package cool.scx.boot;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * todo 插件加载需要重构
 * <p>ScxPlugins class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxPlugins {

    /**
     * Constant <code>pluginsClassList</code>
     */
    public static List<Class<?>> pluginsClassList = new ArrayList<>();

    /**
     * <p>reloadPlugins.</p>
     */
    public static void reloadPlugins() {
        Ansi.ANSI.yellow("ScxPlugins 重新加载中...").ln();
        loadPlugins();
        Ansi.ANSI.yellow("ScxPlugins 重新加载完成...").ln();
    }

    /**
     * <p>initPlugins.</p>
     */
    public static void initPlugins() {
        Ansi.ANSI.yellow("ScxPlugins 初始化中...").ln();
        loadPlugins();
        Ansi.ANSI.yellow("ScxPlugins 初始化完成...").ln();
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
                    Ansi.ANSI.brightRed("找到插件 名称 [" + file.getName() + "] 已禁用!!!").ln();
                }
                return !f;
            }).forEach(file -> {
                        try {
                            PackageUtils.scanPackage(clazz -> {
                                pluginsClassList.add(clazz);
                                return true;
                            }, file.toURI().toURL());
                            Ansi.ANSI.yellow("找到插件 名称 [" + file.getName() + "] 已加载!!!").ln();
                        } catch (Exception e) {
                            Ansi.ANSI.red("找到插件 名称 [" + file.getName() + "] 已损坏!!!").ln();
                        }
                    }
            );
        }
    }
}
