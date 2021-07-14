package cool.scx.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 扫描类工具类
 */
public class ScanClassUtils {

    /**
     * 默认 classLoader
     */
    private static final ClassLoader DEFAULT_CLASS_LOADER = ScanClassUtils.class.getClassLoader();


    /**
     * 读取 jar 包中的所有 class
     *
     * @param jarFileURI jar
     * @return r
     * @throws IOException r
     */
    public static List<Class<?>> getClassListByJar(URI jarFileURI) throws IOException {
        //获取 jarFile
        var jarFile = new JarFile(new File(jarFileURI));
        //获取 jar 包的 classLoader
        var jarClassLoader = new URLClassLoader(new URL[]{jarFileURI.toURL()});
        //进行过滤处理
        return jarFile.stream().filter(jarEntry -> !jarEntry.isDirectory() && jarEntry.getName().endsWith(".class"))
                .map(jarEntry -> loadClass(pathToClassName(jarEntry.getName()), jarClassLoader))
                .collect(Collectors.toList());
    }

    public static List<Class<?>> getClassListByDir(URI classRootDir, ClassLoader classLoader) throws IOException {
        var classList = new ArrayList<Class<?>>();
        var classRootPath = Path.of(classRootDir);
        Files.walkFileTree(classRootPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                //获取 class 的相对路径
                var classRealPath = classRootPath.relativize(path).toString();
                if (classRealPath.endsWith(".class")) {
                    classList.add(loadClass(pathToClassName(classRealPath), classLoader));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classList;
    }

    /**
     * 根据 class 获取地址
     *
     * @param source a {@link java.lang.Class} object.
     * @return 可能是 目录 也可能是 jar 文件
     */
    public static URI getClassSource(Class<?> source) throws URISyntaxException {
        return source.getProtectionDomain().getCodeSource().getLocation().toURI();
    }

    /**
     * 简单封装 (在内部处理异常 )
     *
     * @param className   c
     * @param classLoader c
     * @return c
     */
    private static Class<?> loadClass(String className, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e1) {
            return null;
        }
    }

    /**
     * 根据 basePackage 对 class 进行过滤
     */
    public static List<Class<?>> filterByBasePackage(List<Class<?>> classList, String basePackageName) {
        return classList.stream().filter(c -> c.getPackageName().startsWith(basePackageName)).collect(Collectors.toList());
    }

    public static ClassLoader defaultClassLoader() {
        return DEFAULT_CLASS_LOADER;
    }

    /**
     * 将路径转换为 class 名称
     *
     * @param path p
     * @return c
     */
    private static String pathToClassName(String path) {
        return path.replace(".class", "")//移除尾部的 class
                .replaceAll("\\\\", ".")//windows 路径替换
                .replaceAll("/", ".");//linux 路径替换
    }


}
