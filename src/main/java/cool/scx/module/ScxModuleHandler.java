package cool.scx.module;

import cool.scx.BaseModule;
import cool.scx.Scx;
import cool.scx.ScxEventBus;
import cool.scx.util.Ansi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
     * <p>initModules.</p>
     */
    public static void initModules() {
        for (var scxModule : SCX_MODULE_LIST) {
            Scx.execute(scxModule.baseModuleExample::init);
        }
    }

    /**
     * <p>addModule.</p>
     *
     * @param module a {@link ScxModule} object.
     */
    public static void addModule(ScxModule module) {
        SCX_MODULE_LIST.add(module);
    }

    /**
     * <p>addModule.</p>
     *
     * @param baseModule a T object.
     * @param <T>        a T object.
     */
    public static <T extends BaseModule> void addModule(T baseModule) {
        try {
            SCX_MODULE_LIST.add(new ScxModule(baseModule));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 按照模块顺序迭代 class list
     *
     * @param fun 执行的方法 返回是否中断处理
     */
    public static void iterateClass(Function<Class<?>, Boolean> fun) {
        for (ScxModule scxModule : SCX_MODULE_LIST) {
            for (Class<?> clazz : scxModule.classList) {
                var s = fun.apply(clazz);
                if (!s) {
                    break;
                }
            }
        }
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
                var scxModule = new ScxModule(module);
                SCX_MODULE_LIST.add(new ScxModule(module));
                ScxEventBus.publish(ON_SCX_MODULE_REGISTER_NAME, scxModule);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
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
     * Constant <code>PLUGIN_ROOT</code>
     */
    private static final String PLUGIN_ROOT = "AppRoot:plugins";

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
//        var pluginRoot = FileUtils.getFileByAppRoot(PLUGIN_ROOT);
//        if (pluginRoot.exists()) {
//            var allPluginJar = pluginRoot.listFiles((file, s) -> s.endsWith(".jar"));
//            if (allPluginJar != null) {
//                for (File file : allPluginJar) {
//                    //判断是否被禁用
//                    var f = ScxConfig.disabledPluginList().contains(file.getName());
//                    if (f) {
//                        Ansi.OUT.brightRed("找到插件 名称 [" + file.getName() + "] 已禁用!!!").ln();
//                    } else {
//                        try {
//                            ScxModuleItem moduleByFile = ScxModuleHandler.getModuleByFile(file);
//                            moduleByFile.isPlugin = true;
//                            ScxModuleHandler.addModule(moduleByFile);
//                            Ansi.OUT.yellow("找到插件 文件名称 [" + file.getName() + "] 插件名称 [" + moduleByFile.moduleName + "] 已加载!!!").ln();
//                        } catch (Exception e) {
//                            Ansi.OUT.red("找到插件 文件名称 [" + file.getName() + "] 已损坏 !!!").ln();
//                        }
//                    }
//                }
//            }
//            Ansi.OUT.yellow("共加载 " + ScxModuleHandler.getAllPluginModule().size() + " 个插件 !!!").ln();
//        } else {
//            Ansi.OUT.red("插件目录不存在 未加载任何插件!!!").ln();
//        }
    }

}
