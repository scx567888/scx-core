package cool.scx.boot;

import cool.scx.auth.AuthHandler;
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
 * @author scx56
 * @version $Id: $Id
 */
public final class ScxModuleHandler {

    private static final List<ScxModule> SCX_MODULE_LIST = new ArrayList<>();

    private static ScxModule ROOT_SCX_MODULE = null;

    static {
        initInternalModule();
    }

    /**
     * 添加核心模块
     */
    public static void initInternalModule() {
        ScxModule internalAuthModule = new ScxModule();
        internalAuthModule.moduleClass = null;
        internalAuthModule.moduleName = "内部认证模块";
        internalAuthModule.isPlugin = false;
        internalAuthModule.basePackage = AuthHandler.class.getPackageName();
        internalAuthModule.classList = getClassList(AuthHandler.class, internalAuthModule.basePackage);
        internalAuthModule.moduleRootPath = getModuleRootPath(AuthHandler.class);

        ScxModule internalBaseModule = new ScxModule();
        internalBaseModule.moduleClass = null;
        internalBaseModule.moduleName = "内部基本模块";
        internalBaseModule.isPlugin = false;
        internalBaseModule.basePackage = BaseModule.class.getPackageName();
        internalBaseModule.classList = getClassList(BaseModule.class, internalBaseModule.basePackage);
        internalBaseModule.moduleRootPath = getModuleRootPath(BaseModule.class);

        addModule(internalAuthModule);
        addModule(internalBaseModule);
    }

    /**
     * <p>initModules.</p>
     *
     * @param modules an array of T[] objects.
     */
    public static <T extends BaseModule> void initModules(T[] modules) {
        for (int i = 0; i < modules.length; i++) {
            var tempScxModule = getModuleByBaseModule(modules[i]);
            // 最后一个通过代码注入的模块
            if (i == modules.length - 1) {
                ROOT_SCX_MODULE = tempScxModule;
            }
            addModule(tempScxModule);
        }
    }

    /**
     * <p>addModule.</p>
     *
     * @param module a {@link cool.scx.boot.ScxModule} object.
     */
    public static void addModule(ScxModule module) {
        SCX_MODULE_LIST.add(module);
    }

    /**
     * <p>addModule.</p>
     *
     * @param baseModule a T object.
     */
    public static <T extends BaseModule> void addModule(T baseModule) {
        SCX_MODULE_LIST.add(getModuleByBaseModule(baseModule));
    }

    /**
     * todo
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.boot.ScxModule} object.
     * @throws java.lang.Exception if any.
     */
    public static ScxModule getModuleByFile(File file) throws Exception {
        ScxModule tempModule = new ScxModule();
        tempModule.moduleName = "";
        tempModule.isPlugin = false;
        return tempModule;
    }

    private static <T extends BaseModule> ScxModule getModuleByBaseModule(T module) {
        ScxModule t = new ScxModule();
        t.moduleClass = module.getClass();
        t.moduleName = module.moduleName() != null ? module.moduleName() : t.moduleClass.getSimpleName();
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
        var file = new File(getClassSourceRealPath(modelClass).getFile());
        return file.getPath().endsWith(".jar") ? file.getParentFile() : file;
    }


    /**
     * <p>getBasePackages.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getAllModuleBasePackages() {
        return SCX_MODULE_LIST.stream().map(m -> m.basePackage).toArray(String[]::new);
    }

    /**
     * <p>iterateClass.</p>
     *
     * @param fun a {@link java.util.function.Function} object.
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
     * 项目根模块 这里使用 initModules 里的最后一项
     *
     * @return a {@link cool.scx.boot.ScxModule} object.
     */
    public static ScxModule getRootModule() {
        return ROOT_SCX_MODULE;
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
     * 所有插件 模块
     *
     * @return a {@link java.util.List} object.
     */
    public static List<ScxModule> getAllPluginModule() {
        return SCX_MODULE_LIST.stream().filter(scxModule -> scxModule.isPlugin).collect(Collectors.toList());
    }
}
