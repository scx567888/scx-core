package cool.scx.boot;

import cool.scx.ScxCoreApp;
import cool.scx.config.ScxCmsConfig;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.server.ScxServer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>ScxApp class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxApp {

    private static final Set<Class<?>> classSources = new LinkedHashSet<>();

    private static String[] parameters = new String[0];

    /**
     * <p>run.</p>
     *
     * @param source a {@link java.lang.Class} object.
     * @param args   a {@link java.lang.String} object.
     */
    public static void run(Class<?> source, String... args) {
        run(new Class[]{source}, args);
    }

    /**
     * <p>run.</p>
     *
     * @param source an array of {@link java.lang.Class} objects.
     * @param args   a {@link java.lang.String} object.
     */
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
        ScxServer.init();
        ScxGui.init();
        ScxLicense.init();
    }

    /**
     * <p>getAppClassSources.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public static Class<?> getAppClassSources() {
        return getClassSources().length == 1 ? getClassSources()[0] : getClassSources()[1];
    }

    /**
     * <p>Getter for the field <code>classSources</code>.</p>
     *
     * @return an array of {@link java.lang.Class} objects.
     */
    public static Class<?>[] getClassSources() {
        return classSources.toArray(Class[]::new);
    }

    /**
     * <p>Getter for the field <code>parameters</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getParameters() {
        return parameters;
    }
}
