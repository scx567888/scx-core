package cool.scx.module;

import cool.scx.Scx;
import cool.scx.ScxModule;
import cool.scx.util.Ansi;

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
 * @author scx567888
 * @version 1.1.2
 */
public final class ScxModuleHandler {

    /**
     * 将 BASE_MODULE_ARRAY 进行初始化之后的 ModuleItem 集合
     */
    private static final List<ScxModuleItem> SCX_MODULE_ITEMS = new ArrayList<>();

    /**
     * 默认的核心包 APP KEY (密码) , 注意请不要在您自己的模块中使用此常量 , 非常不安全
     */
    private static final String DEFAULT_APP_KEY = "SCX-123456";

    /**
     * 原始模块 数组
     */
    private static ScxModule[] BASE_MODULE_ARRAY;

    /**
     * 项目根模块 所在路径
     * 默认取 所有自定义模块的最后一个 所在的文件根目录
     */
    private static File APP_ROOT_PATH;

    /**
     * 项目的 appKey
     * 默认取 所有自定义模块的最后一个的AppKey
     */
    private static String APP_KEY = DEFAULT_APP_KEY;

    /**
     * <p>initModules.</p>
     */
    public static void initModules() {
        for (ScxModule baseModule : BASE_MODULE_ARRAY) {
            Scx.execute(baseModule::init);
            var tempScxModule = getModuleByBaseModule(baseModule);
            addModule(tempScxModule);
        }
    }

    /**
     * <p>addModule.</p>
     *
     * @param module a {@link cool.scx.module.ScxModuleItem} object.
     */
    public static void addModule(ScxModuleItem module) {
        SCX_MODULE_ITEMS.add(module);
    }

    /**
     * <p>addModule.</p>
     *
     * @param baseModule a T object.
     * @param <T>        a T object.
     */
    public static <T extends ScxModule> void addModule(T baseModule) {
        SCX_MODULE_ITEMS.add(getModuleByBaseModule(baseModule));
    }

    private static <T extends ScxModule> ScxModuleItem getModuleByBaseModule(T module) {
        ScxModuleItem t = new ScxModuleItem();
        t.moduleClass = module.getClass();
        t.moduleName = t.moduleClass.getSimpleName();
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
     * @throws java.io.IOException         if any.
     * @throws java.net.URISyntaxException if any.
     */
    public static ArrayList<Class<?>> getClassListByJar(URL jarFileUrl) throws IOException, URISyntaxException {
        var classList = new ArrayList<Class<?>>();
        var entries = new JarFile(jarFileUrl.toURI().getPath()).entries();
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
            return Class.forName(className, false, ScxModuleHandler.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getClassByName(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, ScxModuleHandler.class.getClassLoader());
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
        try {
            File file = new File(getClassSourceRealPath(modelClass).toURI().getPath());
            return file.getPath().endsWith(".jar") ? file.getParentFile() : file;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * <p>getBasePackages.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getAllModuleBasePackages() {
        return SCX_MODULE_ITEMS.stream().map(m -> m.basePackage).toArray(String[]::new);
    }

    /**
     * 按照模块顺序迭代 class list
     *
     * @param fun 执行的方法 返回是否中断处理
     */
    public static void iterateClass(Function<Class<?>, Boolean> fun) {
        for (ScxModuleItem scxModule : SCX_MODULE_ITEMS) {
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
    public static List<ScxModuleItem> getAllModule() {
        return SCX_MODULE_ITEMS;
    }

    /**
     * 装载模块 并初始化项目所在目录(APP_ROOT_PATH)
     *
     * @param modules an array of T[] objects.
     * @param <T>     a T object.
     */
    public static <T extends ScxModule> void loadModules(T[] modules) {
        BASE_MODULE_ARRAY = modules;
        var lastModule = BASE_MODULE_ARRAY[BASE_MODULE_ARRAY.length - 1];
        APP_ROOT_PATH = getModuleRootPath(lastModule.getClass());
        if (lastModule.appKey() != null) {
            APP_KEY = lastModule.appKey();
        }
        if (DEFAULT_APP_KEY.equals(APP_KEY)) {
            Ansi.OUT.red("注意!!! 检测到使用了默认的 DEFAULT_APP_KEY , 这是非常不安全的 , 建议重写自定义模块的 appKey() 方法以设置自定义的 APP_KEY !!!").ln();
        }
    }

    /**
     * <p>appRootPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File appRootPath() {
        return APP_ROOT_PATH;
    }

    /**
     * 启动模块的生命周期
     */
    public static void startModules() {
        for (ScxModule baseModule : BASE_MODULE_ARRAY) {
            Scx.execute(baseModule::start);
        }
    }

    /**
     * <p>stopModules.</p>
     */
    public static void stopModules() {
        for (ScxModule baseModule : BASE_MODULE_ARRAY) {
            Scx.execute(baseModule::stop);
        }
    }

    /**
     * 获取 appKey.
     *
     * @return a {@link java.lang.String} object
     */
    public static String appKey() {
        return APP_KEY;
    }

}
