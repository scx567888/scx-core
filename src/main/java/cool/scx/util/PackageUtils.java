package cool.scx.util;

import cool.scx.config.ScxConfig;
import cool.scx.plugin.ScxPlugins;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarFile;

/**
 * <p>PackageUtils class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class PackageUtils {

    /**
     * <p>scanPackageIncludePlugins.</p>
     *
     * @param fun             a {@link java.util.function.Consumer} object.
     * @param classOrJarPaths a {@link java.net.URL} object.
     */
    public static void scanPackageIncludePlugins(Function<Class<?>, Boolean> fun, URL... classOrJarPaths) {
        ScxPlugins.pluginsClassList.forEach(fun::apply);
        scanPackage(fun, classOrJarPaths);
    }

    /**
     * <p>scanPackage.</p>
     *
     * @param fun             a {@link java.util.function.Consumer} object.
     * @param classOrJarPaths a {@link java.net.URL} object.
     */
    public static void scanPackage(Function<Class<?>, Boolean> fun, URL... classOrJarPaths) {
        var classList = new HashSet<Class<?>>();
        if (classOrJarPaths.length == 0) {
            classOrJarPaths = Arrays.stream(ScxConfig.classSources()).map(PackageUtils::getClassSourceRealPath).toArray(URL[]::new);
        }
        for (var c : classOrJarPaths) {
            try {
                if (c.toString().endsWith(".jar")) {
                    classList.addAll(getClassListByJar(c));
                } else {
                    classList.addAll(getClassListByFile(c));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (var clazz : classList) {
            var s = fun.apply(clazz);
            if (!s) {
                break;
            }
        }
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

    private static List<Class<?>> getClassListByFile(URL url) throws URISyntaxException, IOException {
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

    private static Class<?> getClassByName(String className) {
        try {
            return Class.forName(className, false, PackageUtils.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>scanPackageByJar.</p>
     *
     * @param jarFileUrl a {@link java.net.URL} object.
     * @return a {@link java.util.List} object.
     * @throws java.io.IOException if any.
     */
    public static List<Class<?>> getClassListByJar(URL jarFileUrl) throws IOException {
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

    private static Class<?> getClassByName(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, PackageUtils.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException classNotFoundException) {
                return null;
            }
        }
    }

    /**
     * <p>getFileByAppRoot.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public static File getFileByAppRoot(String path) {
        return path.startsWith("absPath:") ? new File(path.replaceAll("absPath:", "")) : new File(getAppRoot(), path);
    }

    /**
     * <p>getAppRoot.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File getAppRoot() {
        var file = new File(getClassSourceRealPath(ScxConfig.getAppClassSources()).getFile());
        return file.getPath().endsWith(".jar") ? file.getParentFile() : file;
    }

    /**
     * <p>getBasePackages.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getBasePackages() {
        return Arrays.stream(ScxConfig.classSources()).map(Class::getPackageName).toArray(String[]::new);
    }

}
