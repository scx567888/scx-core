package cool.scx.module;

import cool.scx.base.BaseModule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 模块 Handler
 *
 * @author 司昌旭
 * @version 1.1.2
 */
public final class ScxModule {

    /**
     * 将 BASE_MODULE_ARRAY 进行初始化之后的 ModuleItem 集合
     * plugin (插件模块) 也会注册到这里
     */
    private static final List<ModuleItem> MODULE_ITEM_LIST = new ArrayList<>();
    /**
     * 原始模块 数组
     */
    private static BaseModule[] BASE_MODULE_ARRAY;
    /**
     * 项目根模块 所在路径
     * 默认取 所有自定义模块的最后一个 所在的文件根目录
     */
    private static File APP_ROOT_PATH;

    /**
     * <p>initModules.</p>
     */
    public static void initModules() {
        for (BaseModule baseModule : BASE_MODULE_ARRAY) {
            var tempScxModule = getModuleByBaseModule(baseModule);
            addModule(tempScxModule);
        }
    }

    /**
     * <p>addModule.</p>
     *
     * @param module a {@link cool.scx.module.ModuleItem} object.
     */
    public static void addModule(ModuleItem module) {
        MODULE_ITEM_LIST.add(module);
    }

    /**
     * <p>addModule.</p>
     *
     * @param baseModule a T object.
     * @param <T>        a T object.
     */
    public static <T extends BaseModule> void addModule(T baseModule) {
        MODULE_ITEM_LIST.add(getModuleByBaseModule(baseModule));
    }

    /**
     * todo
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.module.ModuleItem} object.
     * @throws java.lang.Exception if any.
     */
    public static ModuleItem getModuleByFile(File file) throws Exception {
        ModuleItem tempModule = new ModuleItem();
        tempModule.moduleName = file.getName();
        tempModule.isPlugin = false;
        tempModule.classList = getClassListByJar(file.toURI().toURL());
        tempModule.moduleRootPath = file.getParentFile();
        return tempModule;
    }

    private static <T extends BaseModule> ModuleItem getModuleByBaseModule(T module) {
        ModuleItem t = new ModuleItem();
        t.moduleClass = module.getClass();
        t.moduleName = t.moduleClass.getSimpleName();
        t.isPlugin = false;
        t.basePackage = t.moduleClass.getPackageName();
        t.classList = getClassList(t.moduleClass, t.basePackage);
        t.moduleRootPath = getModuleRootPath(t.moduleClass);
        return t;
    }

    private static ArrayList<Class<?>> getClassListByFile(URL url) throws URISyntaxException, IOException {
        var classList = new ArrayList<Class<?>>();
        var rootFilePath = Path.of(url.toURI());
        Files.walkFileTree(rootFilePath, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                var classRealPath = rootFilePath.relativize(path).toString();
                if (classRealPath.endsWith(".class")) {
                    var className = classRealPath.replace(".class", "").replaceAll("\\\\", ".").replaceAll("/", ".");
                    classList.add(getClassByName(className));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        return classList;
    }

    /**
     * <p>scanPackageByJar.</p>
     *
     * @param jarFileUrl a {@link java.net.URL} object.
     * @return a {@link java.util.List} object.
     * @throws java.io.IOException if any.
     */
    public static ArrayList<Class<?>> getClassListByJar(URL jarFileUrl) throws IOException {
        var classList = new ArrayList<Class<?>>();
        var entries = new JarFile(jarFileUrl.getFile()).entries();
        var jarClassLoader = new URLClassLoader(new URL[]{jarFileUrl});//获得类加载器
        while (entries.hasMoreElements()) {
            var jarEntry = entries.nextElement();
            String jarName = jarEntry.getRealName();
            if (!jarEntry.isDirectory() && jarName.endsWith(".class")) {
                var className = jarName.replace(".class", "").replaceAll("/", ".");
                classList.add(getClassByName(className, jarClassLoader));
            }
        }
        return classList;
    }

    private static Class<?> getClassByName(String className) {
        try {
            return Class.forName(className, false, ScxModule.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getClassByName(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, ScxModule.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException classNotFoundException) {
                return null;
            }
        }
    }

    /**
     * 根据 moduleClass 获取所有的 Class
     *
     * @param moduleClass a {@link java.util.function.Consumer} object.
     * @return a {@link java.util.List} object.
     */
    private static List<Class<?>> getClassList(Class<?> moduleClass, String basePackage) {
        URL moduleClassUrl = getClassSourceRealPath(moduleClass);
        var tempClassList = new ArrayList<Class<?>>();
        try {
            if (moduleClassUrl.toString().endsWith(".jar")) {
                tempClassList = getClassListByJar(moduleClassUrl);
            } else {
                tempClassList = getClassListByFile(moduleClassUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempClassList.stream().filter(c -> c.getPackageName().startsWith(basePackage)).collect(Collectors.toList());
    }

    /**
     * <p>getClassSourceRealPath.</p>
     *
     * @param source a {@link java.lang.Class} object.
     * @return a {@link java.net.URL} object.
     */
    public static URL getClassSourceRealPath(Class<?> source) {
        return source.getProtectionDomain().getCodeSource().getLocation();
    }

    /**
     * <p>getAppRoot.</p>
     *
     * @return a {@link java.io.File} object.
     */
    private static File getModuleRootPath(Class<?> modelClass) {
        var file = new File(getClassSourceRealPath(modelClass).getFile());
        return file.getPath().endsWith(".jar") ? file.getParentFile() : file;
    }


    /**
     * <p>getBasePackages.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getAllModuleBasePackages() {
        return MODULE_ITEM_LIST.stream().map(m -> m.basePackage).toArray(String[]::new);
    }

    /**
     * <p>iterateClass.</p>
     *
     * @param fun a {@link java.util.function.Function} object.
     */
    public static void iterateClass(Function<Class<?>, Boolean> fun) {
        for (ModuleItem scxModule : MODULE_ITEM_LIST) {
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
    public static List<ModuleItem> getAllModule() {
        return MODULE_ITEM_LIST;
    }

    /**
     * 所有插件 模块
     *
     * @return a {@link java.util.List} object.
     */
    public static List<ModuleItem> getAllPluginModule() {
        return MODULE_ITEM_LIST.stream().filter(scxModule -> scxModule.isPlugin).collect(Collectors.toList());
    }

    /**
     * 装载模块 并初始化项目所在目录(APP_ROOT_PATH)
     *
     * @param modules an array of T[] objects.
     * @param <T>     a T object.
     */
    public static <T extends BaseModule> void loadModules(T[] modules) {
        BASE_MODULE_ARRAY = modules;
        var lastModule = BASE_MODULE_ARRAY[BASE_MODULE_ARRAY.length - 1];
        APP_ROOT_PATH = getModuleRootPath(lastModule.getClass());
    }

    /**
     * <p>appRootPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File appRootPath() {
        return APP_ROOT_PATH;
    }

}
