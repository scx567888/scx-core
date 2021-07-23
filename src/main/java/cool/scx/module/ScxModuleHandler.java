package cool.scx.module;

import cool.scx.BaseModule;
import cool.scx.Scx;
import cool.scx.ScxEventBus;
import cool.scx.ScxEventNames;
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
     * 将 BASE_MODULE_ARRAY 进行初始化之后的 ModuleItem 集合
     */
    private static final List<ScxModule> SCX_MODULE_LIST = new ArrayList<>();

    /**
     * Constant <code>PLUGIN_ROOT</code>
     */
    private static final String PLUGIN_ROOT = "AppRoot:plugins";

    static {
        //Bean 加载完毕后的消费者
        ScxEventBus.consumer(ScxEventNames.onContextRegister, o -> {
            var scxModuleList = ScxUtils.cast(o);
            for (ScxModule scxModule : scxModuleList) {
                Scx.execute(scxModule.baseModuleExample::start);
            }
        });
    }

    /**
     * <p>addModule.</p>
     *
     * @param module a {@link cool.scx.module.ScxModule} object.
     */
    public static void addModule(ScxModule module) {
        SCX_MODULE_LIST.add(module);
        ScxEventBus.publish(ScxEventNames.onScxModuleRegister, List.of(module));
    }

    /**
     * <p>addModule.</p>
     *
     * @param moduleName a {@link java.lang.String} object
     */
    public static void removeModule(String moduleName) {
        var needRemoveModule = findModule(moduleName);
        ScxEventBus.publish(ScxEventNames.onScxModuleRemove, List.of(needRemoveModule));
        SCX_MODULE_LIST.removeIf(scxModule -> scxModule.moduleName.equalsIgnoreCase(moduleName));
    }

    /**
     * <p>findModule.</p>
     *
     * @param moduleName a {@link java.lang.String} object
     * @return a {@link cool.scx.module.ScxModule} object
     */
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
        ScxEventBus.publish(ScxEventNames.onScxModuleRegister, SCX_MODULE_LIST);
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

}
