package cool.scx.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.boot.ScxParameters;
import cool.scx.config.example.DataSource;
import cool.scx.config.example.Scx;
import cool.scx.exception.ConfigFileMissingException;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;
import cool.scx.util.Tidy;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 配置文件类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxConfig {

    /**
     * 当前 默认配置文件的实例
     * 注意!!! 如果未执行 init 或 loadConfig 方法 nowScxExample 可能为空
     */
    private static Scx nowScxExample;

    /**
     * 当前 默认配置文件的JsonNode
     * 注意!!! 如果未执行 init 或 loadConfig 方法 nowScxConfigJsonNode 可能为空
     */
    private static JsonNode nowScxConfigJsonNode;


    /**
     * 获取 JsonNode 转换器
     *
     * @param aClass a {@link java.lang.Class} object.
     * @return a {@link java.util.function.Function} object.
     */
    public static Function<JsonNode, Object> getJsonNodeConverter(Class<?> aClass) {
        if (aClass == String.class) {
            return JsonNode::asText;
        } else if (aClass == Integer.class) {
            return JsonNode::asInt;
        } else if (aClass == Long.class) {
            return JsonNode::asLong;
        } else if (aClass == Double.class) {
            return JsonNode::asDouble;
        } else if (aClass == Boolean.class) {
            return JsonNode::asBoolean;
        } else if (aClass == HashSet.class) {
            return c -> {
                var tempSet = new HashSet<String>();
                c.forEach(cc -> tempSet.add(cc.asText()));
                return tempSet;
            };
        } else if (aClass == ArrayList.class) {
            return c -> {
                var tempList = new ArrayList<String>();
                c.forEach(cc -> tempList.add(cc.asText()));
                return tempList;
            };
        } else {
            return c -> c;
        }
    }

    /**
     * 获取环境变量参数转换器
     *
     * @param aClass a {@link java.lang.Class} object.
     * @return a {@link java.util.function.Function} object.
     */
    public static Function<String, Object> getParameterConverter(Class<?> aClass) {
        if (aClass == String.class) {
            return a -> a;
        } else if (aClass == Integer.class) {
            return Integer::parseInt;
        } else if (aClass == Boolean.class) {
            return Boolean::parseBoolean;
        } else if (aClass == Long.class) {
            return Long::parseLong;
        } else if (aClass == Double.class) {
            return Double::parseDouble;
        } else if (aClass == HashSet.class) {
            return a -> new HashSet<>(Arrays.asList(a.split(",")));
        } else if (aClass == ArrayList.class) {
            return a -> Arrays.asList(a.split(","));
        } else {
            return a -> a;
        }
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
    public static <T> T value(String keyPath, T defaultVal) {
        return value(keyPath, defaultVal, Tidy::NoCode, Tidy::NoCode);
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
    public static <T> T value(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun) {
        var jsonNodeVal = nowScxConfigJsonNode;
        var aClass = defaultVal.getClass();
        var convertFun = getJsonNodeConverter(aClass);
        var convertArgFun = getParameterConverter(aClass);

        for (String parameter : ScxParameters.parameters()) {
            if (parameter.startsWith("--" + keyPath + "=")) {
                String[] split = parameter.split("=");
                if (split.length == 2) {
                    T c = (T) convertArgFun.apply(split[1]);
                    successFun.accept(c);
                    return c;
                }
            }
        }
        var split = keyPath.split("\\.");
        for (String s : split) {
            try {
                jsonNodeVal = jsonNodeVal.get(s);
            } catch (Exception ignored) {
                failFun.accept(defaultVal);
                return defaultVal;
            }
        }
        if (jsonNodeVal != null) {
            T c = (T) convertFun.apply(jsonNodeVal);
            successFun.accept(c);
            return c;
        } else {
            failFun.accept(defaultVal);
            return defaultVal;
        }
    }

    /**
     * 初始化 配置文件
     */
    public static void initConfig() {
        Ansi.OUT.brightBlue("ScxConfig 初始化中...").ln();
        loadConfig();
        Ansi.OUT.brightBlue("ScxConfig 初始化完成...").ln();
    }

    /**
     * 加载 配置文件
     */
    private static void loadConfig() {
        var scxConfigJson = new File(PackageUtils.getAppRoot(), "scx-config.json");
        var mapper = new ObjectMapper();
        var rootNode = mapper.nullNode();
        try {
            if (!scxConfigJson.exists()) {
                throw new ConfigFileMissingException();
            }
            rootNode = mapper.readTree(scxConfigJson);
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
        //说明没有读取到 正确的 json 文件
        nowScxConfigJsonNode = rootNode;
        nowScxExample = Scx.from(scxConfigJson, nowScxConfigJsonNode);
    }

    /**
     * <p>tokenKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String tokenKey() {
        return "S-Token";
    }

    /**
     * <p>deviceKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String deviceKey() {
        return "S-Device";
    }

    /**
     * <p>openHttps.</p>
     *
     * @return a boolean.
     */
    public static boolean openHttps() {
        return nowScxExample.https.isOpen;
    }

    /**
     * <p>port.</p>
     *
     * @return a int.
     */
    public static int port() {
        return nowScxExample.port;
    }

    /**
     * <p>sslPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File sslPath() {
        return nowScxExample.https.sslPathValue;
    }

    /**
     * <p>sslPassword.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String sslPassword() {
        return nowScxExample.https.sslPasswordValue;
    }

    /**
     * <p>showLog.</p>
     *
     * @return a boolean.
     */
    public static boolean showLog() {
        return nowScxExample.showLog;
    }

    /**
     * <p>fixTable.</p>
     *
     * @return a boolean.
     */
    public static boolean fixTable() {
        return nowScxExample.fixTable;
    }

    /**
     * <p>realDelete.</p>
     *
     * @return a boolean.
     */
    public static boolean realDelete() {
        return nowScxExample.realDelete;
    }

    /**
     * <p>loginErrorLockTimes.</p>
     *
     * @return a int.
     */
    public static int loginErrorLockTimes() {
        return nowScxExample.loginErrorLockTimes;
    }

    /**
     * <p>loginErrorLockSecond.</p>
     *
     * @return a long.
     */
    public static long loginErrorLockSecond() {
        return nowScxExample.loginErrorLockSecond;
    }

    /**
     * <p>cmsRoot.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsRoot() {
        return nowScxExample.cms.rootValue;
    }

    /**
     * <p>pluginRoot.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File pluginRoot() {
        return nowScxExample.plugin.rootValue;
    }

    /**
     * <p>pluginDisabledList.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public static Set<String> pluginDisabledList() {
        return nowScxExample.plugin.disabledList;
    }

    /**
     * <p>bodyLimit.</p>
     *
     * @return a long.
     */
    public static long bodyLimit() {
        return nowScxExample.bodyLimitValue;
    }


    /**
     * <p>allowedOrigin.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String allowedOrigin() {
        return nowScxExample.allowedOrigin;
    }

    /**
     * <p>cmsResourceUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cmsResourceUrl() {
        return nowScxExample.cms.resourceHttpUrl;
    }

    /**
     * <p>cmsResourceLocations.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsResourceLocations() {
        return nowScxExample.cms.resourceLocationsValue;
    }

    /**
     * 数据源配置
     *
     * @return a {@link java.lang.String} object.
     */
    public static DataSource dataSource() {
        return nowScxExample.dataSource;
    }

    /**
     * <p>dateTimeFormatter.</p>
     *
     * @return a {@link java.time.format.DateTimeFormatter} object.
     */
    public static DateTimeFormatter dateTimeFormatter() {
        return nowScxExample.dateTimeFormatter;
    }

    /**
     * <p>uploadFilePath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File uploadFilePath() {
        return nowScxExample.uploadFilePathValue;
    }

    /**
     * <p>scxVersion.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String scxVersion() {
        return "1.0.16";
    }

    /**
     * <p>license.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String license() {
        return nowScxExample.license;
    }

    /**
     * <p>cmsResourceSuffix.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cmsTemplateSuffix() {
        return nowScxExample.cms.templateSuffix;
    }

    /**
     * <p>confusionLoginError.</p>
     *
     * @return a boolean.
     */
    public static boolean confusionLoginError() {
        return nowScxExample.confusionLoginError;
    }

    /**
     * <p>AppKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String AppKey() {
        return "H8QS91GcuNGP9735";
    }


}
