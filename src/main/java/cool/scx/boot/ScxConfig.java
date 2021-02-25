package cool.scx.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.enumeration.Color;
import cool.scx.util.*;

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
     * Constant <code>AppKey="H8QS91GcuNGP9735"</code>
     */
    public static final String AppKey = "H8QS91GcuNGP9735";
    /**
     * Constant <code>tokenKey="S-Token"</code>
     */
    public static final String tokenKey = "S-Token";
    /**
     * Constant <code>cookieKey="S-Cookie"</code>
     */
    public static final String cookieKey = "S-Cookie";
    /**
     * 核心包版本
     */
    public static final String coreVersion = "0.8.2";
    /**
     * Constant <code>scxConfigJsonNode</code>
     */
    public static final JsonNode scxConfigJsonNode;
    /**
     * Constant <code>uploadFilePath</code>
     */
    public static final File uploadFilePath;
    /**
     * Constant <code>dataSourceUrl=""</code>
     */
    public static final String dataSourceUrl;
    /**
     * Constant <code>dataSourceUsername=""</code>
     */
    public static final String dataSourceUsername;
    /**
     * Constant <code>dataSourcePassword=""</code>
     */
    public static final String dataSourcePassword;
    /**
     * Constant <code>confusionLoginError=</code>
     */
    public static final boolean confusionLoginError;
    /**
     * Constant <code>license=""</code>
     */
    public static final String license;
    /**
     * Constant <code>cmsRoot</code>
     */
    public static final File cmsRoot;
    /**
     * Constant <code>cmsResourceUrl=""</code>
     */
    public static final String cmsResourceUrl;
    /**
     * Constant <code>cmsResourceLocations</code>
     */
    public static final File cmsResourceLocations;
    /**
     * Constant <code>cmsResourceSuffix=""</code>
     */
    public static final String cmsResourceSuffix;
    /**
     *
     */
    public static final File cmsFaviconIcoPath;
    /**
     * Constant <code>showLog=</code>
     */
    public static final boolean showLog;
    /**
     * Constant <code>showGui=</code>
     */
    public static final boolean showGui;
    /**
     * Constant <code>realDelete=</code>
     */
    public static final boolean realDelete;
    /**
     * Constant <code>port=</code>
     */
    public static final int port;
    /**
     * Constant <code>allowedOrigin=""</code>
     */
    public static final String allowedOrigin;
    /**
     * Constant <code>loginErrorLockTimes=</code>
     */
    public static final int loginErrorLockTimes;
    /**
     * Constant <code>loginErrorLockSecond=</code>
     */
    public static final int loginErrorLockSecond;
    /**
     * Constant <code>fixTable=</code>
     */
    public static final boolean fixTable;
    /**
     * Constant <code>pluginRoot</code>
     */
    public static final File pluginRoot;
    /**
     * Constant <code>pluginDisabledList</code>
     */
    public static final Set<String> pluginDisabledList;
    /**
     * Constant <code>dateTimeFormatter</code>
     */
    public static final DateTimeFormatter dateTimeFormatter;
    /**
     * Constant <code>openHttps=</code>
     */
    public static final boolean openHttps;
    /**
     * ssh 证书路径
     */
    public static final File certificatePath;
    /**
     * ssh 证书密码
     */
    public static final String certificatePassword;

    /**
     * request body 大小限制
     */
    public static final long bodyLimit;


    static {
        StringUtils.println("ScxConfig v" + coreVersion + " 初始化中...", Color.BRIGHT_BLUE);

        scxConfigJsonNode = getScxJsonConfig();

        port = getConfigValue("scx.port", 8080,
                s -> StringUtils.println("✔ 服务器 IP 地址                        \t -->\t " + NetUtils.getLocalAddress() + "\r\n✔ 端口号                                \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.port                  \t -->\t 已采用默认值 : " + f, Color.RED),
                c -> ScxConfig.checkPort(c.asInt()), a -> ScxConfig.checkPort(Integer.parseInt(a)));

        pluginRoot = PackageUtils.getFileByAppRoot(getConfigValue("scx.plugin.root", "/plugins/",
                s -> StringUtils.println("✔ 插件根目录                           \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.plugin.root             \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asText, a -> a));

        pluginDisabledList = getConfigValue("scx.plugin.disabled-list", new HashSet<>(),
                s -> StringUtils.println("✔ 禁用插件列表                           \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.plugin.disabled-list     \t -->\t 已采用默认值 : " + f, Color.RED),
                c -> {
                    var tempSet = new HashSet<String>();
                    c.forEach(cc -> tempSet.add(cc.asText()));
                    return tempSet;
                }, a -> new HashSet<>(Arrays.asList(a.split(","))));

        uploadFilePath = getConfigValue("scx.file-path", PackageUtils.getFileByAppRoot("/scxUploadFile/"),
                s -> StringUtils.println("✔ 文件上传目录                           \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.file-path             \t -->\t 已采用默认值 : " + f, Color.RED),
                c -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        bodyLimit = getConfigValue("scx.body-limit", 10000L,
                s -> StringUtils.println("✔ 请求体大小限制                          \t -->\t " + FileUtils.longToDisplaySize(s), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.body-limit             \t -->\t 已采用默认值 : " + FileUtils.longToDisplaySize(f), Color.RED),
                c -> FileUtils.displaySizeToLong(c.asText()), FileUtils::displaySizeToLong);

        confusionLoginError = getConfigValue("scx.confusion-login-error", false,
                s -> StringUtils.println("✔ 是否混淆登录错误                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.confusion-login-error \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asBoolean, Boolean::valueOf);

        loginErrorLockTimes = getConfigValue("scx.login-error-lock-times", 999,
                s -> StringUtils.println("✔ 登录错误锁定次数                     \t -->\t " + s + " 次", Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.login-error-lock-times \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asInt, Integer::valueOf);

        loginErrorLockSecond = getConfigValue("scx.login-error-lock-second", 10,
                s -> StringUtils.println("✔ 登录错误锁定的时间                    \t -->\t " + s + " 秒", Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.login-error-lock-second \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asInt, Integer::valueOf);

        showLog = getConfigValue("scx.show-log", true,
                s -> StringUtils.println("✔ 是否将错误日志打印到控制台              \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.show-log              \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        showGui = getConfigValue("scx.show-gui", false,
                s -> StringUtils.println("✔ 是否将显示 GUI                      \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.show-gui              \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        realDelete = getConfigValue("scx.real-delete", false,
                s -> StringUtils.println("✔ 数据库删除方式为                       \t -->\t " + (s ? "物理删除" : "逻辑删除"), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.real-delete           \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        license = getConfigValue("scx.license", null, ScxConfig::NoCode,
                f -> StringUtils.println("✘ 未检测到 scx.license               \t -->\t 请检查 license 是否正确", Color.RED), JsonNode::asText, (a) -> a);

        openHttps = getConfigValue("scx.https.is-open", true,
                s -> StringUtils.println("✔ 是否开启 https                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.https.is-open            \t -->\t 已采用默认值 : ", Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        certificatePath = getConfigValue("scx.https.certificate-path", PackageUtils.getFileByAppRoot("/certificate/scx_dev.jks"),
                s -> StringUtils.println("✔ 证书路径                           \t -->\t " + s.getPath(), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.https.certificate-path        \t -->\t 请检查证书路径是否正确", Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        certificatePassword = getConfigValue("scx.https.certificate-password", "",
                s -> NoCode(),
                f -> StringUtils.println("✘ 未检测到 scx.license               \t -->\t 请检查证书密码是否正确", Color.RED), c -> CryptoUtils.decryptText(c.asText()), CryptoUtils::decryptText);

        dateTimeFormatter = DateTimeFormatter.ofPattern(getConfigValue("scx.date-time-pattern", "yyyy-MM-dd HH:mm:ss",
                s -> StringUtils.println("✔ 日期格式为                          \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.date-time-pattern        \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a));

        cmsRoot = getConfigValue("scx.cms.root", PackageUtils.getFileByAppRoot("/c/"),
                s -> StringUtils.println("✔ Cms 根目录                         \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.cms.root              \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        cmsResourceUrl = getConfigValue("scx.cms.resource-url", "/static/*",
                s -> StringUtils.println("✔ Cms 静态资源 Url                      \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.cms.resource-url         \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a);

        cmsResourceLocations = getConfigValue("scx.cms.resource-locations", PackageUtils.getFileByAppRoot("/c/static"),
                s -> StringUtils.println("✔ Cms 静态资源目录                       \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.cms.resource-locations   \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        cmsResourceSuffix = getConfigValue("scx.cms.resource-suffix", ".html",
                s -> StringUtils.println("✔ Cms 静态资源后缀                       \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.cms.resource-suffix   \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, a -> a);

        cmsFaviconIcoPath = getConfigValue("scx.cms.favicon-ico-path", PackageUtils.getFileByAppRoot("/c/favicon.ico"),
                s -> StringUtils.println("✔ Cms Favicon Ico 路径                  \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.cms.favicon-ico-path   \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        allowedOrigin = getConfigValue("scx.allowed-origin", "*",
                s -> StringUtils.println("✔ 允许的请求源                           \t -->\t " + s, Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.allowed-origin           \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a);

        dataSourceUrl = getConfigValue("scx.data-source.url");

        dataSourceUsername = getConfigValue("scx.data-source.username");

        dataSourcePassword = CryptoUtils.decryptText(getConfigValue("scx.data-source.password"));

        fixTable = getConfigValue("scx.fix-table", false,
                s -> StringUtils.println("✔ 修复数据表                          \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> StringUtils.println("✘ 未检测到 scx.fix-table               \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asBoolean, Boolean::valueOf);

    }

    private static int checkPort(int p) {
        while (NetUtils.isLocalePortUsing(p)) {
            p = p + 1;
            StringUtils.println("✘ 端口号 [ " + (p - 1) + " ] 已被占用 !!!         \t -->\t 新端口号 : " + p, Color.RED);
        }
        return p;
    }

    /**
     * <p>getScxJsonConfig.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.JsonNode} object.
     */
    public static JsonNode getScxJsonConfig() {
        JsonNode rootNode = null;
        var mapper = new ObjectMapper();
        try {
            var scxConfigJsons = PackageUtils.getAppRoot().listFiles(file -> file.isFile() && file.getName().startsWith("scx") && file.getName().endsWith(".json"));
            rootNode = mapper.readTree(scxConfigJsons[0]);
            StringUtils.println("✔ 已加载配置文件                       \t -->\t " + scxConfigJsons[0].getPath(), Color.GREEN);
        } catch (Exception e) {
            StringUtils.println("✘ 配置文件已损坏!!!", Color.RED);
        }
        return rootNode;
    }


    /**
     * <p>getConfigValue.</p>
     *
     * @param keyPath a {@link java.lang.String} object.
     * @param <T>     a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath) {
        return getConfigValue(keyPath, null);
    }

    /**
     * <p>getConfigValue.</p>
     *
     * @param keyPath    a {@link java.lang.String} object.
     * @param defaultVal a T object.
     * @param <T>        a T object.
     * @return a T object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getConfigValue(String keyPath, T defaultVal) {
        return (T) getConfigValue(keyPath, defaultVal, ScxConfig::NoCode, ScxConfig::NoCode, c -> {
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
     * <p>getConfigValue.</p>
     *
     * @param keyPath       a {@link java.lang.String} object.
     * @param defaultVal    a T object.
     * @param successFun    a {@link java.util.function.Consumer} object.
     * @param failFun       a {@link java.util.function.Consumer} object.
     * @param convertFun    a {@link java.util.function.Function} object.
     * @param convertArgFun a {@link java.util.function.Function} object.
     * @param <T>           a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun, Function<JsonNode, T> convertFun, Function<String, T> convertArgFun) {
        return getConfigValue(keyPath, defaultVal, successFun, failFun, convertFun, convertArgFun, scxConfigJsonNode);
    }

    /**
     * <p>getConfigValue.</p>
     *
     * @param keyPath       a {@link java.lang.String} object.
     * @param defaultVal    a T object.
     * @param successFun    a {@link java.util.function.Consumer} object.
     * @param failFun       a {@link java.util.function.Consumer} object.
     * @param convertFun    a {@link java.util.function.Function} object.
     * @param convertArgFun a {@link java.util.function.Function} object.
     * @param jsonNodeVal   a {@link com.fasterxml.jackson.databind.JsonNode} object.
     * @param <T>           a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun, Function<JsonNode, T> convertFun, Function<String, T> convertArgFun, JsonNode jsonNodeVal) {
        for (String parameter : ScxApp.getParameters()) {
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

    //为了保持 lambda 表达式的整洁

    /**
     * <p>NoCode.</p>
     *
     * @param objects a {@link java.lang.Object} object.
     */
    public static void NoCode(Object... objects) {

    }

    /**
     * <p>init.</p>
     */
    public static void init() {
        StringUtils.println("ScxConfig 初始化完成...", Color.BRIGHT_BLUE);
    }

}
