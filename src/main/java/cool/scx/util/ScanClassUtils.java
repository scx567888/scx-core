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
 *
 * @author scx567888
 * @version 1.3.0
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
     * @throws java.io.IOException r
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

    /**
     * <p>getClassListByDir.</p>
     *
     * @param classRootDir a {@link java.net.URI} object
     * @param classLoader  a {@link java.lang.ClassLoader} object
     * @return a {@link java.util.List} object
     * @throws java.io.IOException if any.
     */
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
     * @throws java.net.URISyntaxException if any.
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
     *
     * @param classList       a {@link java.util.List} object
     * @param basePackageName a {@link java.lang.String} object
     * @return a {@link java.util.List} object
     */
    public static List<Class<?>> filterByBasePackage(List<Class<?>> classList, String basePackageName) {
        return classList.stream().filter(c -> c.getPackageName().startsWith(basePackageName)).collect(Collectors.toList());
    }

    /**
     * <p>defaultClassLoader.</p>
     *
     * @return a {@link java.lang.ClassLoader} object
     */
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

    /**
     * <p>isJar.</p>
     *
     * @param file a {@link java.io.File} object
     * @return a boolean
     */
    public static boolean isJar(File file) {
        return !file.isDirectory() && file.getPath().endsWith(".jar");
    }

}
