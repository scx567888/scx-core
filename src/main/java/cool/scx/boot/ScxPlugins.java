package cool.scx.boot;

import cool.scx.enumeration.Color;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
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

    static {
        StringUtils.println("ScxPlugins 初始化中...", Color.YELLOW);
        var pluginsRoot = ScxConfig.pluginRoot;
        if (pluginsRoot.exists()) {
            Arrays.stream(pluginsRoot.listFiles()).filter(file -> file.getName().endsWith(".jar")).filter(file -> {
                var f = ScxConfig.pluginDisabledList.contains(file.getName());
                if (f) {
                    StringUtils.println("找到插件 名称 [" + file.getName() + "] 已禁用!!!", Color.BRIGHT_RED);
                }
                return !f;
            }).forEach(file -> {
                        try {
                            PackageUtils.scanPackageByJar(clazz -> pluginsClassList.add(clazz), file.toURI().toURL());
                            StringUtils.println("找到插件 名称 [" + file.getName() + "] 已加载!!!", Color.YELLOW);
                        } catch (Exception e) {
                            StringUtils.println("找到插件 名称 [" + file.getName() + "] 已损坏!!!", Color.RED);
                        }
                    }
            );
        }
    }

    /**
     * <p>init.</p>
     */
    public static void init() {
        StringUtils.println("ScxPlugins 初始化完成...", Color.YELLOW);
    }
}
