package cool.scx.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.enumeration.Color;
import cool.scx.util.CryptoUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ScxConfig {
    public static final String AppKey = "H8QS91GcuNGP9735";
    public static final String tokenKey = "S-Token";
    public static final String coreVersion = "0.3.4";
    public static final JsonNode scxConfigJsonNode;
    public static final File uploadFilePath;
    public static final String dataSourceUrl;
    public static final String dataSourceUsername;
    public static final String dataSourcePassword;
    public static final boolean confusionLoginError;
    public static final String license;
    public static final File cmsRoot;
    public static final String cmsResourceUrl;
    public static final File cmsResourceLocations;
    public static final String cmsResourceSuffix;
    public static final boolean showLog;
    public static final boolean showGui;
    public static final boolean realDelete;
    public static final int port;
    public static final String allowedOrigin;
    public static final int loginErrorLockTimes;
    public static final int loginErrorLockSecond;
    public static final boolean fixTable;
    public static final File pluginRoot;
    public static final Set<String> pluginDisabledList;
    public static final DateTimeFormatter dateTimeFormatter;
    public static final boolean openHttps;
    public static final File certificatePath;
    public static final String certificatePassword;

    public static final String[] checkPermsUrls = new String[]{
            "/api/*",
    };
    public static final String[] excludeCheckPermsUrls = new String[]{
            "/api/user/login",
            "/api/user/login",
            "/api/user/login",
    };

    static {
        StringUtils.println("ScxConfig v" + coreVersion + " 初始化中...", Color.BRIGHT_BLUE);

        scxConfigJsonNode = getScxJsonConfig();

        port = getConfigValue("scx.port", 8080,
                (s) -> {
                    StringUtils.println("✔ 服务器 IP 地址                        \t -->\t " + NetUtils.getLocalAddress(), Color.GREEN);
                    StringUtils.println("✔ 端口号                                \t -->\t " + s, Color.GREEN);
                },
                (f) -> StringUtils.println("✘ 未检测到 scx.port                  \t -->\t 已采用默认值 : " + f, Color.RED),
                ScxConfig::checkPort, Integer::valueOf);

        pluginRoot = getConfigValue("scx.plugin.root", PackageUtils.getFileByAppRoot("/plugins/"),
                (s) -> StringUtils.println("✔ 插件根目录                           \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.plugin.root             \t -->\t 已采用默认值 : " + f, Color.RED),
                (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        pluginDisabledList = getConfigValue("scx.plugin.disabled-list", new HashSet<>(),
                (s) -> StringUtils.println("✔ 禁用插件列表                           \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.plugin.disabled-list     \t -->\t 已采用默认值 : " + f, Color.RED),
                (c) -> {
                    var tempSet = new HashSet<String>();
                    c.forEach(cc -> tempSet.add(cc.asText()));
                    return tempSet;
                }, (a) -> new HashSet<>(Arrays.asList(a.split(","))));

        uploadFilePath = getConfigValue("scx.file-path", PackageUtils.getFileByAppRoot("/scxUploadFile/"),
                (s) -> StringUtils.println("✔ 文件上传目录                           \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.file-path             \t -->\t 已采用默认值 : " + f, Color.RED),
                (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        confusionLoginError = getConfigValue("scx.confusion-login-error", false,
                (s) -> StringUtils.println("✔ 是否混淆登录错误                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.confusion-login-error \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asBoolean, Boolean::valueOf);

        loginErrorLockTimes = getConfigValue("scx.login-error-lock-times", 999,
                (s) -> StringUtils.println("✔ 登录错误锁定次数                     \t -->\t " + s + " 次", Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.login-error-lock-times \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asInt, Integer::valueOf);

        loginErrorLockSecond = getConfigValue("scx.login-error-lock-second", 10,
                (s) -> StringUtils.println("✔ 登录错误锁定的时间                    \t -->\t " + s + " 秒", Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.login-error-lock-second \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asInt, Integer::valueOf);

        showLog = getConfigValue("scx.show-log", true,
                (s) -> StringUtils.println("✔ 是否将错误日志打印到控制台              \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.show-log              \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        showGui = getConfigValue("scx.show-gui", false,
                (s) -> StringUtils.println("✔ 是否将显示 GUI                      \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.show-gui              \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        realDelete = getConfigValue("scx.real-delete", false,
                (s) -> StringUtils.println("✔ 数据库删除方式为                       \t -->\t " + (s ? "物理删除" : "逻辑删除"), Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.real-delete           \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        license = getConfigValue("scx.license", null,
                (s) -> NoCode(),
                (f) -> StringUtils.println("✘ 未检测到 scx.license               \t -->\t 请检查 license 是否正确", Color.RED), JsonNode::asText, (a) -> a);

        openHttps = getConfigValue("scx.https.is-open", true,
                (s) -> StringUtils.println("✔ 是否开启 https                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.https.is-open            \t -->\t 已采用默认值 : ", Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        certificatePath = getConfigValue("scx.https.certificate-path", PackageUtils.getFileByAppRoot("/certificate/scx_dev.jks"),
                (s) -> StringUtils.println("✔ 证书路径                           \t -->\t " + s.getPath(), Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.https.certificate-path        \t -->\t 请检查证书路径是否正确", Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        certificatePassword = getConfigValue("scx.https.certificate-password", "",
                (s) -> NoCode(),
                (f) -> StringUtils.println("✘ 未检测到 scx.license               \t -->\t 请检查证书密码是否正确", Color.RED), c -> CryptoUtils.decryptText(c.asText()), CryptoUtils::decryptText);

        dateTimeFormatter = DateTimeFormatter.ofPattern(getConfigValue("scx.date-time-pattern", "yyyy-MM-dd HH:mm:ss",
                (s) -> StringUtils.println("✔ 日期格式为                          \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.date-time-pattern        \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a));

        cmsRoot = getConfigValue("scx.cms.root", PackageUtils.getFileByAppRoot("/c/"),
                (s) -> StringUtils.println("✔ Cms 根目录                         \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.cms.root              \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        cmsResourceUrl = getConfigValue("scx.cms.resource-url", "/static/*",
                (s) -> StringUtils.println("✔ Cms 静态资源 Url                      \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.cms.resource-url         \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a);

        cmsResourceLocations = getConfigValue("scx.cms.resource-locations", PackageUtils.getFileByAppRoot("/c/static"),
                (s) -> StringUtils.println("✔ Cms 静态资源目录                       \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.cms.resource-locations   \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        cmsResourceSuffix = getConfigValue("scx.cms.resource-suffix", ".html",
                (s) -> StringUtils.println("✔ Cms 静态资源后缀                       \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.cms.resource-suffix   \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, a -> a);

        allowedOrigin = getConfigValue("scx.allowed-origin", "*",
                (s) -> StringUtils.println("✔ 允许的请求源                           \t -->\t " + s, Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.allowed-origin           \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a);

        dataSourceUrl = getConfigValue("scx.data-source.url", null, (s) -> NoCode(), (f) -> NoCode(), JsonNode::asText, (a) -> a);

        dataSourceUsername = getConfigValue("scx.data-source.username", null, (s) -> NoCode(), (f) -> NoCode(), JsonNode::asText, (a) -> a);

        dataSourcePassword = getConfigValue("scx.data-source.password", null, (s) -> NoCode(), (f) -> NoCode(), (c) -> CryptoUtils.decryptText(c.asText()), CryptoUtils::decryptText);

        fixTable = getConfigValue("scx.fix-table", false,
                (s) -> StringUtils.println("✔ 修复数据表                          \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                (f) -> StringUtils.println("✘ 未检测到 scx.fix-table               \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asBoolean, Boolean::valueOf);

    }

    private static int checkPort(JsonNode c) {
        var p = c.asInt();
        while (NetUtils.isLocalePortUsing(p)) {
            p = p + 1;
            StringUtils.println("✘ 端口号 [ " + (p - 1) + " ] 已被占用 !!!         \t -->\t 新端口号 : " + p, Color.RED);
        }
        return p;
    }

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

    public static <T> T getConfigValue(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun, Function<JsonNode, T> convertFun, Function<String, T> convertArgFun) {
        return getConfigValue(keyPath, defaultVal, successFun, failFun, convertFun, convertArgFun, scxConfigJsonNode);
    }

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


    //为了保持 lambda 表达式的整洁
    public static void NoCode() {

    }

    public static void init() {
        StringUtils.println("ScxConfig 初始化完成...", Color.BRIGHT_BLUE);
    }

}
