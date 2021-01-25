package cool.scx.util;

import cool.scx.boot.ScxApp;
import cool.scx.boot.ScxPlugins;

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
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public class PackageUtils {

    public static void scanPackageIncludePlugins(Consumer<Class<?>> fun, URL... classOrJarPaths) {
        ScxPlugins.pluginsClassList.forEach(fun);
        scanPackage(fun, classOrJarPaths);
    }

    public static void scanPackage(Consumer<Class<?>> fun, URL... classOrJarPaths) {
        if (classOrJarPaths.length == 0) {
            classOrJarPaths = Arrays.stream(ScxApp.getClassSources()).map(PackageUtils::getClassSourceRealPath).toArray(URL[]::new);
        }
        for (URL c : classOrJarPaths) {
            try {
                if (c.toString().endsWith(".jar")) {
                    scanPackageByJar(fun, c);
                } else {
                    scanPackageByFile(fun, c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static URL getClassSourceRealPath(Class<?> source) {
        return source.getProtectionDomain().getCodeSource().getLocation();
    }

    private static void scanPackageByFile(Consumer<Class<?>> fun, URL url) throws URISyntaxException, IOException {
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
                    var className = classRealPath.replace(".class", "").replaceAll("\\\\", ".");
                    fun.accept(getClassByName(className));
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

    }

    private static Class<?> getClassByName(String className) {
        try {
            return Class.forName(className, false, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void scanPackageByJar(Consumer<Class<?>> fun, URL jarFileUrl) throws IOException {
        var entries = new JarFile(jarFileUrl.getFile()).entries();
        var jarClassLoader = new URLClassLoader(new URL[]{jarFileUrl});//获得类加载器
        while (entries.hasMoreElements()) {
            var jarEntry = entries.nextElement();
            String jarName = jarEntry.getRealName();
            if (!jarEntry.isDirectory() && jarName.endsWith(".class")) {
                var className = jarName.replace(".class", "").replaceAll("/", ".");
                fun.accept(getClassByName(className, jarClassLoader));
            }
        }
    }

    private static Class<?> getClassByName(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException classNotFoundException) {
                return null;
            }
        }
    }

    public static File getFileByAppRoot(String path) {
        return path.startsWith("absPath:") ? new File(path.replaceAll("absPath:", "")) : new File(getAppRoot(), path);
    }

    public static File getAppRoot() {
        var file = new File(getClassSourceRealPath(ScxApp.getAppClassSources()).getFile());
        return file.getPath().endsWith(".jar") ? file.getParentFile() : file;
    }

    public static String[] getBasePackages() {
        return Arrays.stream(ScxApp.getClassSources()).map(Class::getPackageName).toArray(String[]::new);
    }

}
