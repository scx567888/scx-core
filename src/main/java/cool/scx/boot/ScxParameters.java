package cool.scx.boot;

import cool.scx.ScxCoreApp;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * 最基本的参数处理
 */
public class ScxParameters {
    private static Class<?>[] classSources;
    private static String[] parameters;

    public static void initParameters(Class<?>[] _classSources, String[] _args) {
        classSources = filterClassSource(_classSources);
        parameters = _args;
    }


    private static Class<?>[] filterClassSource(Class<?>[] args) {
        //利用 set 进行 过滤
        //以保证 参数都是未重复的
        var tempSet = new LinkedHashSet<Class<?>>();
        tempSet.add(ScxCoreApp.class);
        tempSet.addAll(Arrays.asList(args));
        //返回处理后的数组
        return tempSet.toArray(Class<?>[]::new);
    }

    /**
     * 在 classSource 中寻找 程序的 主运行 class
     * 后续会以此 以确定 程序运行的路径
     * 并以此为标准获取 配置文件 等
     *
     * @return a {@link java.lang.Class} object.
     */
    public static Class<?> getAppClassSources() {
        //因为 classSources 第一位永远是 ScxCoreApp 所以做此处理
        return classSources.length == 1 ? classSources[0] : classSources[1];
    }

    /**
     * 获取 从外部传来的参数 (java -jar scx.jar  xxx)
     *
     * @return 外部传来的参数
     */
    public static String[] parameters() {
        return parameters;
    }

    /**
     * <p>classSources.</p>
     *
     * @return an array of {@link java.lang.Class} objects.
     */
    public static Class<?>[] classSources() {
        return classSources;
    }
}
