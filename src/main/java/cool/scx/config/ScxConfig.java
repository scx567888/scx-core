package cool.scx.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import cool.scx.Scx;
import cool.scx.exception.ConfigFileMissingException;
import cool.scx.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * 配置文件类
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class ScxConfig {

    /**
     * SCX 版本号
     */
    public static final String SCX_VERSION = "1.3.0";

    /**
     * 配置文件 路径
     */
    public static final String SCX_CONFIG_PATH = "AppRoot:scx-config.xml";

    /**
     * 默认 LocalDateTime 格式化类
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 当前 默认配置文件的实例
     * 是一个映射表
     * 注意!!! 如果未执行 init 或 loadConfig 方法 nowScxExample 可能为空
     */
    private static final ConfigExample CONFIG_EXAMPLE = new ConfigExample();

    /**
     * 通过 命令行 (外部) 传来的原始 参数
     */
    private static String[] ORIGINAL_PARAMS;

    /**
     * config 简单使用 实例
     */
    private static EasyToUseConfig easyToUseConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScxConfig.class);

    /**
     * 初始化 配置文件
     *
     * @param params a {@link java.lang.String} object.
     */
    public static void initConfig(String... params) {
        ORIGINAL_PARAMS = params;
        loadJsonConfig();
        loadParamsConfig();
        loadEasyToUseConfig();
        Scx.execute(ScxConfig::watchConfig);
        Ansi.out().green("ScxConfig 初始化完成...").println();
    }

    /**
     * 加载 外部参数 config
     */
    private static void loadParamsConfig() {
        for (String arg : ORIGINAL_PARAMS) {
            if (arg.startsWith("--")) {
                var strings = arg.substring(2).split("=");
                if (strings.length == 2) {
                    CONFIG_EXAMPLE.add(strings[0], strings[1]);
                }
            }
        }
    }

    /**
     * 加载 配置文件
     */
    private static void loadJsonConfig() {
        var scxConfigFile = FileUtils.getFileByAppRoot(SCX_CONFIG_PATH);
        try {
            if (!scxConfigFile.exists()) {
                throw new ConfigFileMissingException();
            }
            CONFIG_EXAMPLE.add(XmlUtils.readToMap(scxConfigFile));
            Ansi.out().brightBlue("已加载配置文件  " + scxConfigFile.getPath()).println();
        } catch (Exception e) {
            if (e instanceof JsonProcessingException) {
                Ansi.out().red("N 配置文件已损坏!!! 请确保配置文件正确 scx-config.xml").println();
            } else if (e instanceof ConfigFileMissingException) {
                Ansi.out().red("N 配置文件已丢失!!! 请确保配置文件存在 scx-config.xml").println();
            } else {
                e.printStackTrace();
            }
        }
        logConfiguration();
    }

    /**
     * 加载 config 简单使用 实例
     */
    private static void loadEasyToUseConfig() {
        easyToUseConfig = new EasyToUseConfig();
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
    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(String keyPath, T defaultVal) {
        if (defaultVal != null) {
            var tClass = (Class<T>) defaultVal.getClass();
            var o = get(keyPath, tClass);
            return o == null ? defaultVal : o;
        } else {
            return (T) get(keyPath);
        }
    }

    /**
     * 从指定的配置文件获取配置值 并自动判断类型
     * 没有找到配置文件会返回 默认值
     *
     * @param keyPath keyPath
     * @return a T object.
     */
    public static Object get(String keyPath) {
        return CONFIG_EXAMPLE.get(keyPath);
    }

    public static <T> T get(String keyPath, Class<T> type) {
        return CONFIG_EXAMPLE.get(keyPath, type);
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
     * <p>realDelete.</p>
     *
     * @return a boolean.
     */
    public static boolean realDelete() {
        return easyToUseConfig.realDelete;
    }

    /**
     * 获取模板根路径
     *
     * @return a {@link java.io.File} object.
     */
    public static File templateRoot() {
        return easyToUseConfig.templateRoot;
    }


    /**
     * 获取模板资源前缀
     *
     * @return a {@link java.lang.String} object.
     */
    public static String templateResourceHttpUrl() {
        return easyToUseConfig.templateResourceHttpUrl;
    }

    /**
     * 获取模板资源根路径
     *
     * @return a {@link java.io.File} object.
     */
    public static File templateResourceRoot() {
        return easyToUseConfig.templateResourceRoot;
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
    public static ConfigExample getConfigExample() {
        return CONFIG_EXAMPLE;
    }

    private static void watchConfig() {
        var path = Scx.appRoot();
        var configPath = FileUtils.getFileByAppRoot(SCX_CONFIG_PATH).toPath();
        WatchFileUtils.watchDir(path.toPath(), new WatchFileEvent() {
            @Override
            public void onModify(Path path) {
                if (path.equals(configPath)) {
                    Ansi.out().green("ScxConfig 已修改,重新加载中...").println();
                    loadJsonConfig();
                    loadEasyToUseConfig();
                    Ansi.out().green("ScxConfig 重新加载完成...").println();
                }
            }

        });
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
     * <p>pluginDisabledList.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public static Set<String> disabledPlugins() {
        return easyToUseConfig.disabledPluginList;
    }

    /**
     * todo 显示 config 内容
     */
    private static void logConfiguration() {
        LOGGER.debug("{} - configuration:", "ScxConfig");
        final var propertyNames = List.of(1, 2, 3, 4, 5, 6, 7, 7);
        for (var prop : propertyNames) {
            try {
                var value = 123;
//                if ("dataSourceProperties".equals(prop)) {
//                    var dsProps = PropertyElf.copyProperties(dataSourceProperties);
//                    dsProps.setProperty("password", "<masked>");
//                    value = dsProps;
//                }
//
//                if ("initializationFailTimeout".equals(prop) && initializationFailTimeout == Long.MAX_VALUE) {
//                    value = "infinite";
//                }
//                else if ("transactionIsolation".equals(prop) && transactionIsolationName == null) {
//                    value = "default";
//                }
//                else if (prop.matches("scheduledExecutorService|threadFactory") && value == null) {
//                    value = "internal";
//                }
//                else if (prop.contains("jdbcUrl") && value instanceof String) {
//                    value = ((String)value).replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
//                }
//                else if (prop.contains("password")) {
//                    value = "<masked>";
//                }
//                else if (value instanceof String) {
//                    value = "\"" + value + "\""; // quote to see lead/trailing spaces is any
//                }
//                else if (value == null) {
//                    value = "none";
//                }
                LOGGER.warn("{}{}", (prop + "................................................").substring(0, 32), value);
            } catch (Exception e) {
                // continue
            }
        }
    }

}
