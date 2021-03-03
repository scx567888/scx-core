package cool.scx.boot;

import cool.scx.config.ScxConfig;
import cool.scx.util.log.Color;
import cool.scx.util.log.LogUtils;
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
        LogUtils.println("ScxPlugins 重新加载中...", Color.YELLOW);
        loadPlugins();
        LogUtils.println("ScxPlugins 重新加载完成...", Color.YELLOW);
    }

    /**
     * <p>initPlugins.</p>
     */
    public static void initPlugins() {
        LogUtils.println("ScxPlugins 初始化中...", Color.YELLOW);
        loadPlugins();
        LogUtils.println("ScxPlugins 初始化完成...", Color.YELLOW);
    }

    /**
     * <p>init.</p>
     */
    private static void loadPlugins() {
        LogUtils.println("ScxPlugins 初始化中...", Color.YELLOW);
        var pluginsRoot = ScxConfig.pluginRoot();
        if (pluginsRoot.exists()) {
            Arrays.stream(pluginsRoot.listFiles()).filter(file -> file.getName().endsWith(".jar")).filter(file -> {
                var f = ScxConfig.pluginDisabledList().contains(file.getName());
                if (f) {
                    LogUtils.println("找到插件 名称 [" + file.getName() + "] 已禁用!!!", Color.BRIGHT_RED);
                }
                return !f;
            }).forEach(file -> {
                        try {
                            PackageUtils.scanPackage(clazz -> {
                                pluginsClassList.add(clazz);
                                return true;
                            }, file.toURI().toURL());
                            LogUtils.println("找到插件 名称 [" + file.getName() + "] 已加载!!!", Color.YELLOW);
                        } catch (Exception e) {
                            LogUtils.println("找到插件 名称 [" + file.getName() + "] 已损坏!!!", Color.RED);
                        }
                    }
            );
        }
    }
}
