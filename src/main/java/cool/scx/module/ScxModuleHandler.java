package cool.scx.module;

import cool.scx.BaseModule;
import cool.scx.Scx;
import cool.scx.ScxEventBus;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;
import cool.scx.util.ScxUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模块 Handler
 *
 * @author scx567888
 * @version 1.1.2
 */
public final class ScxModuleHandler {

    /**
     * ScxModule 注册时事件名称
     */
    public static final String ON_SCX_MODULE_REGISTER_NAME = "onScxModuleRegister";

    /**
     * ScxModule 移除时事件名称
     */
    public static final String ON_SCX_MODULE_REMOVE_NAME = "onScxModuleRemove";

    /**
     * 将 BASE_MODULE_ARRAY 进行初始化之后的 ModuleItem 集合
     */
    private static final List<ScxModule> SCX_MODULE_LIST = new ArrayList<>();

    /**
     * Constant <code>PLUGIN_ROOT</code>
     */
    private static final String PLUGIN_ROOT = "AppRoot:plugins";

    static {
        //Bean 加载完毕后的消费者
        ScxEventBus.consumer(ScxContext.ON_CONTEXT_REGISTER_NAME, o -> {
            var scxModuleList = ScxUtils.cast(o);
            for (ScxModule scxModule : scxModuleList) {
                Scx.execute(scxModule.baseModuleExample::start);
            }
        });
    }

    /**
     * <p>addModule.</p>
     *
     * @param module a {@link ScxModule} object.
     */
    public static void addModule(ScxModule module) {
        SCX_MODULE_LIST.add(module);
        ScxEventBus.publish(ON_SCX_MODULE_REGISTER_NAME, List.of(module));
    }

    /**
     * <p>addModule.</p>
     *
     * @param module a {@link ScxModule} object.
     */
    public static void removeModule(String moduleName) {
        var needRemoveModule = findModule(moduleName);
        ScxEventBus.publish(ON_SCX_MODULE_REMOVE_NAME, List.of(needRemoveModule));
        SCX_MODULE_LIST.removeIf(scxModule -> scxModule.moduleName.equalsIgnoreCase(moduleName));
    }

    public static ScxModule findModule(String moduleName) {
        return SCX_MODULE_LIST.stream().filter(m -> m.moduleName.equalsIgnoreCase(moduleName)).findAny().orElse(null);
    }

    /**
     * 所有模块
     *
     * @return a {@link java.util.List} object.
     */
    public static List<ScxModule> getAllModule() {
        return SCX_MODULE_LIST;
    }

    /**
     * 装载捆绑的模块 并初始化项目所在目录(APP_ROOT_PATH)
     *
     * @param modules an array of T[] objects.
     * @param <T>     a T object.
     */
    public static <T extends BaseModule> void loadBundledModules(T[] modules) {
        for (T module : modules) {
            try {
                SCX_MODULE_LIST.add(new ScxModule(module));
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
        ScxEventBus.publish(ON_SCX_MODULE_REGISTER_NAME, SCX_MODULE_LIST);
    }

    /**
     * 启动模块的生命周期
     */
    public static void startModules() {
        for (var scxModule : SCX_MODULE_LIST) {
            Scx.execute(scxModule.baseModuleExample::start);
        }
    }

    /**
     * <p>stopModules.</p>
     */
    public static void stopModules() {
        for (var scxModule : SCX_MODULE_LIST) {
            Scx.execute(scxModule.baseModuleExample::stop);
        }
    }

    /**
     * <p>reloadPlugins.</p>
     */
    public static void reloadPlugins() {
        Ansi.out().yellow("ScxPlugins 重新加载中...").println();
        loadPlugins();
        Ansi.out().yellow("ScxPlugins 重新加载完成...").println();
    }

    /**
     * 初始化插件
     */
    public static void initPlugins() {
        Ansi.out().yellow("ScxPlugins 初始化中...").println();
        loadPlugins();
        Ansi.out().yellow("ScxPlugins 初始化完成...").println();
    }

    /**
     * <p>init.</p>
     */
    public static void loadPlugins() {
        var pluginRoot = FileUtils.getFileByAppRoot(PLUGIN_ROOT);
        if (pluginRoot.exists()) {
            var allPluginJar = pluginRoot.listFiles((file, s) -> s.endsWith(".jar"));
            if (allPluginJar != null) {
                for (var file : allPluginJar) {
                    //判断是否被禁用
                    var f = ScxConfig.disabledPlugins().contains(file.getName());
                    if (f) {
                        Ansi.out().brightRed("找到插件 名称 [" + file.getName() + "] 已禁用!!!").println();
                    } else {
                        try {
                            var pluginModule = new ScxModule(file, true);
                            ScxModuleHandler.addModule(pluginModule);
                            Ansi.out().yellow("找到插件 文件名称 [" + file.getName() + "] 插件名称 [" + pluginModule.moduleName + "] 已加载!!!").println();
                        } catch (Exception e) {
                            Ansi.out().red("找到插件 文件名称 [" + file.getName() + "] 已损坏 !!!").println();
                        }
                    }
                }
            }
            Ansi.out().yellow("共加载 " + getAllPluginModule().size() + " 个插件 !!!").println();
        } else {
            Ansi.out().red("插件目录不存在 未加载任何插件!!!").println();
        }
    }

    /**
     * 所有插件 模块
     *
     * @return a {@link java.util.List} object.
     */
    public static List<ScxModule> getAllPluginModule() {
        return SCX_MODULE_LIST.stream().filter(scxModule -> scxModule.isPlugin).collect(Collectors.toList());
    }

}
