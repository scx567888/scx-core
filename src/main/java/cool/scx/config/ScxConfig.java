package cool.scx.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.ScxCoreApp;
import cool.scx.config.example.Scx;
import cool.scx.exception.ConfigFileMissingException;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;
import cool.scx.util.Tidy;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
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
    private static Class<?>[] classSources;
    private static String[] parameters;


    /**
     * 根据文件名获取配置文件
     *
     * @param configName 要获取的配置文件的名称
     * @return 配置文件的文件
     */
    public static File getConfigFile(String configName) {
        var configFile = new File(PackageUtils.getAppRoot(), configName);
        if (configFile.isFile() && configFile.exists()) {
            return configFile;
        } else {
            throw new RuntimeException("配置文件 " + configName + " 不存在 !!!");
        }
    }

    /**
     * 从默认配置文件获取配置值 并自动判断类型
     * 没有找到配置文件会返回 null
     *
     * @param keyPath keyPath
     * @param <T>     a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath) {
        return getConfigValue(keyPath, null);
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
    public static <T> T getConfigValue(String keyPath, T defaultVal) {
        return (T) getConfigValue(keyPath, defaultVal, Tidy::NoCode, Tidy::NoCode, c -> {
            if (c.isArray()) {
                var tempList = new ArrayList<Object>();
                c.forEach(cc -> tempList.add(getValueByJsonNode(cc)));
                return tempList;
            } else {
                return getValueByJsonNode(c);
            }
        }, a -> a);
    }

    /**
     * 从默认配置文件获取配置值 并自动判断类型
     * 没有找到配置文件会返回 默认值
     *
     * @param keyPath       keyPath
     * @param defaultVal    默认值
     * @param successFun    获取成功的回调
     * @param failFun       获取失败的回调
     * @param convertFun    jsonNode 转换的方法 因为获取到的是 jsonNode 类型所以需要手动进行转换
     * @param convertArgFun 外部参数转换的方法
     * @param <T>           a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun, Function<JsonNode, T> convertFun, Function<String, T> convertArgFun) {
        return getConfigValue(keyPath, defaultVal, successFun, failFun, convertFun, convertArgFun, nowScxConfigJsonNode);
    }

    /**
     * 从指定的配置文件获取配置值 并自动判断类型
     * 没有找到配置文件会返回 默认值
     *
     * @param keyPath       keyPath
     * @param defaultVal    默认值
     * @param successFun    获取成功的回调
     * @param failFun       获取失败的回调
     * @param convertFun    jsonNode 转换的方法 因为获取到的是 jsonNode 类型所以需要手动进行转换
     * @param convertArgFun 外部参数转换的方法
     * @param jsonNodeVal   指定的配置文件
     * @param <T>           a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun, Function<JsonNode, T> convertFun, Function<String, T> convertArgFun, JsonNode jsonNodeVal) {
        for (String parameter : parameters) {
            if (parameter.startsWith("--" + keyPath + "=")) {
                String[] split = parameter.split("=");
                if (split.length == 2) {
                    T c = convertArgFun.apply(split[1]);
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
            T c = convertFun.apply(jsonNodeVal);
            successFun.accept(c);
            return c;
        } else {
            failFun.accept(defaultVal);
            return defaultVal;
        }
    }

    /**
     * 根据 jsonNode 的类型自动判断 并获取值
     *
     * @param jsonNode jsonNode
     * @return 值
     */
    private static Object getValueByJsonNode(JsonNode jsonNode) {
        if (jsonNode.isInt()) {
            return jsonNode.asInt();
        }
        if (jsonNode.isLong()) {
            return jsonNode.asLong();
        }
        if (jsonNode.isDouble()) {
            return jsonNode.asDouble();
        }
        if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        }
        return jsonNode.asText();
    }

    /**
     * <p>init.</p>
     *
     * @param _classSources an array of {@link java.lang.Class} objects.
     * @param _args         an array of {@link java.lang.String} objects.
     */
    public static void initConfig(Class<?>[] _classSources, String[] _args) {
        Ansi.ANSI.brightBlue("ScxConfig 初始化中...").ln();
        classSources = filterClassSource(_classSources);
        parameters = _args;
        loadConfig();
        Ansi.ANSI.brightBlue("ScxConfig 初始化完成...").ln();
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
     * <p>reloadConfig.</p>
     */
    public static void reloadConfig() {
        Ansi.ANSI.brightBlue("ScxConfig 重新加载中...").ln();
        loadConfig();
        Ansi.ANSI.brightBlue("ScxConfig 重新加载完成...").ln();
    }

    /**
     * 加载 配置文件
     */
    private static void loadConfig() {
        File configFile;
        var mapper = new ObjectMapper();
        JsonNode rootNode = mapper.nullNode();
        try {
            //获取所有 已 scx 开头 .json 结尾的文件
            //这里假设这些就是 配置文件
            var scxConfigJsons = PackageUtils.getAppRoot().listFiles(file -> file.isFile() && file.getName().startsWith("scx") && file.getName().endsWith(".json"));
            //数量不为空 就返回第一个
            if (scxConfigJsons != null && scxConfigJsons.length > 0) {
                configFile = scxConfigJsons[0];
            } else {
                throw new ConfigFileMissingException();
            }
            rootNode = mapper.readTree(configFile);
            Ansi.ANSI.green("✔ 已加载配置文件                       \t -->\t " + configFile.getPath()).ln();
        } catch (Exception e) {
            configFile = new File(PackageUtils.getAppRoot(), "scx-default.json");
            if (e instanceof JsonProcessingException) {
                Ansi.ANSI.red("✘ 配置文件已损坏!!! 已生成正确的配置文件 scx-default.json").ln();
            } else if (e instanceof ConfigFileMissingException) {
                Ansi.ANSI.red("✘ 配置文件已丢失!!! 已使用默认配置文件 scx-default.json").ln();
            }
        }
        //说明没有读取到 正确的 json 文件
        nowScxConfigJsonNode = rootNode;
        nowScxExample = Scx.from(configFile, nowScxConfigJsonNode);
    }

    /**
     * <p>cookieKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cookieKey() {
        return "S-Cookie";
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
     * <p>certPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File certPath() {
        return nowScxExample.https.certPathValue;
    }

    /**
     * <p>certPassword.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String certPassword() {
        return nowScxExample.https.certificatePasswordValue;
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
     * <p>cmsFaviconIcoPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsFaviconIcoPath() {
        return nowScxExample.cms.faviconIcoPathValue;
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
     * <p>dataSourceUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourceUrl() {
        return nowScxExample.dataSource.url;
    }

    /**
     * <p>dataSourceUsername.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourceUsername() {
        return nowScxExample.dataSource.username;
    }

    /**
     * <p>dataSourcePassword.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourcePassword() {
        return nowScxExample.dataSource.passwordValue;
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
        return "0.9.15";
    }

    /**
     * <p>showGui.</p>
     *
     * @return a boolean.
     */
    public static boolean showGui() {
        return nowScxExample.showGui;
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
