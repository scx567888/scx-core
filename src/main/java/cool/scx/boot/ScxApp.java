package cool.scx.boot;

import cool.scx.ScxCoreApp;
import cool.scx.server.ScxVertxServer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ScxApp {

    private static final Set<Class<?>> classSources = new LinkedHashSet<>();

    private static String[] parameters;

    public static void run(Class<?> source, String... args) {
        run(new Class[]{source}, args);
    }

    public static void run(Class<?>[] source, String... args) {
        parameters = args;
        classSources.add(ScxCoreApp.class);
        classSources.addAll(Arrays.asList(source));
        ScxBanner.init();
        ScxConfig.init();
        ScxPlugins.init();
        ScxCmsConfig.init();
        ScxContext.init();
        ScxListener.init();
        ScxVertxServer.init();
        ScxGui.init();
    }

    public static Class<?> getAppClassSources() {
        return getClassSources().length == 1 ? getClassSources()[0] : getClassSources()[1];
    }

    public static Class<?>[] getClassSources() {
        return classSources.toArray(Class[]::new);
    }

    public static String[] getParameters() {
        return parameters;
    }
}
