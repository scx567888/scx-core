package cool.scx.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.exception.ConfigFileMissingException;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;
import cool.scx.util.MapUtils;
import cool.scx.util.Tidy;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 配置文件类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxConfig {

    /**
     * Constant <code>SCX_VERSION </code>
     */
    public static final String SCX_VERSION = "1.1.5";

    /**
     * Constant <code>SCX_APP_KEY="H8QS91GcuNGP9735"</code>
     */
    public static final String SCX_APP_KEY = "H8QS91GcuNGP9735";

    /**
     * Constant <code>DATETIME_FORMATTER</code>
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 当前 默认配置文件的实例
     * 注意!!! 如果未执行 init 或 loadConfig 方法 nowScxExample 可能为空
     */
    private static final Map<String, Object> CONFIG_EXAMPLE = new HashMap<>();

    /**
     * 通过 命令行 (外部) 传来的原始 参数
     */
    private static String[] ORIGINAL_PARAMS;

    /**
     * config 简单使用 实例
     */
    private static EasyToUseConfig easyToUseConfig;

    /**
     * 初始化 配置文件
     *
     * @param params a {@link java.lang.String} object.
     */
    public static void initConfig(String... params) {
        Ansi.OUT.brightBlue("ScxConfig 初始化中...").ln();
        ORIGINAL_PARAMS = params;
        loadJsonConfig();
        loadParamsConfig();
        loadEasyToUseConfig();
        Ansi.OUT.brightBlue("ScxConfig 初始化完成...").ln();
    }

    /**
     * 加载 外部参数 config
     */
    private static void loadParamsConfig() {
        var map = new HashMap<String, Object>();
        for (String arg : ORIGINAL_PARAMS) {
            if (arg.startsWith("--")) {
                var strings = arg.substring(2).split("=");
                if (strings.length == 2) {
                    map.put(strings[0], strings[1]);
                }
            }
        }
        CONFIG_EXAMPLE.putAll(map);
    }

    /**
     * 加载 配置文件
     */
    private static void loadJsonConfig() {
        var scxConfigJson = FileUtils.getFileByRootModulePath("scx-config.json");
        var mapper = new ObjectMapper();
        try {
            if (!scxConfigJson.exists()) {
                throw new ConfigFileMissingException();
            }
            var jsonConfigMap = mapper.readValue(scxConfigJson, new TypeReference<Map<String, Object>>() {
            });
            CONFIG_EXAMPLE.putAll(MapUtils.flatMap(jsonConfigMap, null));
            Ansi.OUT.green("Y 已加载配置文件                       \t -->\t " + scxConfigJson.getPath()).ln();
        } catch (Exception e) {
            if (e instanceof JsonProcessingException) {
                Ansi.OUT.red("N 配置文件已损坏!!! 已创建正确的配置文件 scx-config.json").ln();
            } else if (e instanceof ConfigFileMissingException) {
                Ansi.OUT.red("N 配置文件已丢失!!! 已创建默认的配置文件 scx-config.json").ln();
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载 config 简单使用 实例
     */
    private static void loadEasyToUseConfig() {
        easyToUseConfig = new EasyToUseConfig();
    }

    /**
     * 从默认配置文件获取配置值
     * 没有找到配置文件会返回 null
     *
     * @param keyPath keyPath
     * @param <T>     a T object.
     * @return a T object.
     */
    public static <T> T get(String keyPath) {
        return get(keyPath, null);
    }

    /**
     * 从默认配置文件获取配置值 并自动判断类型
     * 没有找到配置文件会返回 默认值
     *
     * @param keyPath    keyPath
     * @param defaultVal 默认值
     * @param <T>        a T object.
     * @return a T object.
     */
    public static <T> T get(String keyPath, T defaultVal) {
        return get(keyPath, defaultVal, Tidy::NoCode, Tidy::NoCode);
    }

    /**
     * 从指定的配置文件获取配置值 并自动判断类型
     * 没有找到配置文件会返回 默认值
     *
     * @param keyPath    keyPath
     * @param defaultVal 默认值
     * @param successFun 获取成功的回调
     * @param failFun    获取失败的回调
     * @param <T>        a T object.
     * @return a T object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun) {
        Object o = CONFIG_EXAMPLE.get(keyPath);
        if (o == null) {
            failFun.accept(defaultVal);
            return defaultVal;
        } else {
            T value = (T) o;
            successFun.accept(value);
            return value;
        }
    }

    /**
     * <p>dataSourceHost.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourceHost() {
        return easyToUseConfig.dataSourceHost;
    }

    /**
     * <p>dataSourcePort.</p>
     *
     * @return a int.
     */
    public static int dataSourcePort() {
        return easyToUseConfig.dataSourcePort;
    }

    /**
     * <p>dataSourceDatabase.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourceDatabase() {
        return easyToUseConfig.dataSourceDatabase;
    }

    /**
     * <p>dataSourceParameters.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public static Set<String> dataSourceParameters() {
        return easyToUseConfig.dataSourceParameters;
    }

    /**
     * <p>dataSourceUsername.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourceUsername() {
        return easyToUseConfig.dataSourceUsername;
    }

    /**
     * <p>dataSourcePassword.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourcePassword() {
        return easyToUseConfig.dataSourcePassword;
    }


    /**
     * <p>openHttps.</p>
     *
     * @return a boolean.
     */
    public static boolean isOpenHttps() {
        return easyToUseConfig.isOpenHttps;
    }

    /**
     * <p>port.</p>
     *
     * @return a int.
     */
    public static int port() {
        return easyToUseConfig.port;
    }

    /**
     * <p>sslPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File sslPath() {
        return easyToUseConfig.sslPath;
    }

    /**
     * <p>sslPassword.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String sslPassword() {
        return easyToUseConfig.sslPassword;
    }

    /**
     * <p>showLog.</p>
     *
     * @return a boolean.
     */
    public static boolean showLog() {
        return easyToUseConfig.showLog;
    }

    /**
     * <p>fixTable.</p>
     *
     * @return a boolean.
     */
    public static boolean fixTable() {
        return easyToUseConfig.fixTable;
    }

    /**
     * <p>realDelete.</p>
     *
     * @return a boolean.
     */
    public static boolean realDelete() {
        return easyToUseConfig.realDelete;
    }

    /**
     * <p>cmsRoot.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsRoot() {
        return easyToUseConfig.cmsRoot;
    }

    /**
     * <p>pluginDisabledList.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public static Set<String> disabledPluginList() {
        return easyToUseConfig.disabledPluginList;
    }


    /**
     * <p>allowedOrigin.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String allowedOrigin() {
        return easyToUseConfig.allowedOrigin;
    }

    /**
     * <p>cmsResourceUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cmsResourceHttpUrl() {
        return easyToUseConfig.cmsResourceHttpUrl;
    }

    /**
     * <p>cmsResourceLocations.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsResourceLocations() {
        return easyToUseConfig.cmsResourceLocations;
    }


    /**
     * <p>cmsResourceSuffix.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cmsTemplateSuffix() {
        return easyToUseConfig.cmsTemplateSuffix;
    }


    /**
     * 获取 从外部传来的参数 (java -jar scx.jar  xxx)
     *
     * @return 外部传来的参数
     */
    public static String[] originalParams() {
        return ORIGINAL_PARAMS;
    }

    /**
     * <p>getConfigExample.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, Object> getConfigExample() {
        return CONFIG_EXAMPLE;
    }

}
