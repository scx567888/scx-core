package cool.scx.config;

import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.enumeration.Color;
import cool.scx.util.*;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cool.scx.config.ScxConfig.getConfigValue;

class ScxConfigExample {


    /**
     * Constant <code>uploadFilePath</code>
     */
    public final File uploadFilePath;
    /**
     * Constant <code>dataSourceUrl=""</code>
     */
    public final String dataSourceUrl;
    /**
     * Constant <code>dataSourceUsername=""</code>
     */
    public final String dataSourceUsername;
    /**
     * Constant <code>dataSourcePassword=""</code>
     */
    public final String dataSourcePassword;
    /**
     * Constant <code>confusionLoginError=</code>
     */
    public final boolean confusionLoginError;
    /**
     * Constant <code>license=""</code>
     */
    public final String license;
    /**
     * Constant <code>cmsRoot</code>
     */
    public final File cmsRoot;
    /**
     * Constant <code>cmsResourceUrl=""</code>
     */
    public final String cmsResourceUrl;
    /**
     * Constant <code>cmsResourceLocations</code>
     */
    public final File cmsResourceLocations;
    /**
     * Constant <code>cmsResourceSuffix=""</code>
     */
    public final String cmsResourceSuffix;
    /**
     *
     */
    public final File cmsFaviconIcoPath;
    /**
     * Constant <code>showLog=</code>
     */
    public final boolean showLog;
    /**
     * Constant <code>showGui=</code>
     */
    public final boolean showGui;
    /**
     * Constant <code>realDelete=</code>
     */
    public final boolean realDelete;
    /**
     * Constant <code>port=</code>
     */
    public final int port;
    /**
     * Constant <code>allowedOrigin=""</code>
     */
    public final String allowedOrigin;
    /**
     * Constant <code>loginErrorLockTimes=</code>
     */
    public final int loginErrorLockTimes;
    /**
     * Constant <code>loginErrorLockSecond=</code>
     */
    public final int loginErrorLockSecond;
    /**
     * Constant <code>fixTable=</code>
     */
    public final boolean fixTable;
    /**
     * Constant <code>pluginRoot</code>
     */
    public final File pluginRoot;
    /**
     * Constant <code>pluginDisabledList</code>
     */
    public final Set<String> pluginDisabledList;
    /**
     * Constant <code>dateTimeFormatter</code>
     */
    public final DateTimeFormatter dateTimeFormatter;
    /**
     * Constant <code>openHttps=</code>
     */
    public final boolean openHttps;
    /**
     * ssh 证书路径
     */
    public final File certificatePath;
    /**
     * ssh 证书密码
     */
    public final String certificatePassword;

    /**
     * request body 大小限制
     */
    public final long bodyLimit;


    public ScxConfigExample(JsonNode scxConfigJsonNode) {


        port = getConfigValue("scx.port", 8080,
                s -> LogUtils.println("✔ 服务器 IP 地址                        \t -->\t " + NetUtils.getLocalAddress() + "\r\n✔ 端口号                                \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.port                  \t -->\t 已采用默认值 : " + f, Color.RED),
                c -> ScxConfig.checkPort(c.asInt()), a -> ScxConfig.checkPort(Integer.parseInt(a)));

        pluginRoot = PackageUtils.getFileByAppRoot(getConfigValue("scx.plugin.root", "/plugins/",
                s -> LogUtils.println("✔ 插件根目录                           \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.plugin.root             \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asText, a -> a));

        pluginDisabledList = getConfigValue("scx.plugin.disabled-list", new HashSet<>(),
                s -> LogUtils.println("✔ 禁用插件列表                           \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.plugin.disabled-list     \t -->\t 已采用默认值 : " + f, Color.RED),
                c -> {
                    var tempSet = new HashSet<String>();
                    c.forEach(cc -> tempSet.add(cc.asText()));
                    return tempSet;
                }, a -> new HashSet<>(Arrays.asList(a.split(","))));

        uploadFilePath = getConfigValue("scx.file-path", PackageUtils.getFileByAppRoot("/scxUploadFile/"),
                s -> LogUtils.println("✔ 文件上传目录                           \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.file-path             \t -->\t 已采用默认值 : " + f, Color.RED),
                c -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        bodyLimit = getConfigValue("scx.body-limit", 16777216L,
                s -> LogUtils.println("✔ 请求体大小限制                          \t -->\t " + FileUtils.longToDisplaySize(s), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.body-limit             \t -->\t 已采用默认值 : " + FileUtils.longToDisplaySize(f), Color.RED),
                c -> FileUtils.displaySizeToLong(c.asText()), FileUtils::displaySizeToLong);

        confusionLoginError = getConfigValue("scx.confusion-login-error", false,
                s -> LogUtils.println("✔ 是否混淆登录错误                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.confusion-login-error \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asBoolean, Boolean::valueOf);

        loginErrorLockTimes = getConfigValue("scx.login-error-lock-times", 999,
                s -> LogUtils.println("✔ 登录错误锁定次数                     \t -->\t " + s + " 次", Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.login-error-lock-times \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asInt, Integer::valueOf);

        loginErrorLockSecond = getConfigValue("scx.login-error-lock-second", 10,
                s -> LogUtils.println("✔ 登录错误锁定的时间                    \t -->\t " + s + " 秒", Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.login-error-lock-second \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asInt, Integer::valueOf);

        showLog = getConfigValue("scx.show-log", true,
                s -> LogUtils.println("✔ 是否将错误日志打印到控制台              \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.show-log              \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        showGui = getConfigValue("scx.show-gui", false,
                s -> LogUtils.println("✔ 是否将显示 GUI                      \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.show-gui              \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        realDelete = getConfigValue("scx.real-delete", false,
                s -> LogUtils.println("✔ 数据库删除方式为                       \t -->\t " + (s ? "物理删除" : "逻辑删除"), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.real-delete           \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        license = getConfigValue("scx.license", null, NoCode::NoCode,
                f -> LogUtils.println("✘ 未检测到 scx.license               \t -->\t 请检查 license 是否正确", Color.RED), JsonNode::asText, (a) -> a);

        openHttps = getConfigValue("scx.https.is-open", false,
                s -> LogUtils.println("✔ 是否开启 https                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.https.is-open            \t -->\t 已采用默认值 : ", Color.RED), JsonNode::asBoolean, Boolean::valueOf);

        certificatePath = getConfigValue("scx.https.certificate-path", PackageUtils.getFileByAppRoot("/certificate/scx_dev.jks"),
                s -> LogUtils.println("✔ 证书路径                           \t -->\t " + s.getPath(), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.https.certificate-path        \t -->\t 请检查证书路径是否正确", Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        certificatePassword = getConfigValue("scx.https.certificate-password", "",
                NoCode::NoCode,
                f -> LogUtils.println("✘ 未检测到 scx.license               \t -->\t 请检查证书密码是否正确", Color.RED), c -> CryptoUtils.decryptText(c.asText()), CryptoUtils::decryptText);

        dateTimeFormatter = DateTimeFormatter.ofPattern(getConfigValue("scx.date-time-pattern", "yyyy-MM-dd HH:mm:ss",
                s -> LogUtils.println("✔ 日期格式为                          \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.date-time-pattern        \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a));

        cmsRoot = getConfigValue("scx.cms.root", PackageUtils.getFileByAppRoot("/c/"),
                s -> LogUtils.println("✔ Cms 根目录                         \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.cms.root              \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        cmsResourceUrl = getConfigValue("scx.cms.resource-url", "/static/*",
                s -> LogUtils.println("✔ Cms 静态资源 Url                      \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.cms.resource-url         \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a);

        cmsResourceLocations = getConfigValue("scx.cms.resource-locations", PackageUtils.getFileByAppRoot("/c/static"),
                s -> LogUtils.println("✔ Cms 静态资源目录                       \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.cms.resource-locations   \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        cmsResourceSuffix = getConfigValue("scx.cms.resource-suffix", ".html",
                s -> LogUtils.println("✔ Cms 静态资源后缀                       \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.cms.resource-suffix   \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, a -> a);

        cmsFaviconIcoPath = getConfigValue("scx.cms.favicon-ico-path", PackageUtils.getFileByAppRoot("/c/favicon.ico"),
                s -> LogUtils.println("✔ Cms Favicon Ico 路径                  \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.cms.favicon-ico-path   \t -->\t 已采用默认值 : " + f, Color.RED), (c) -> PackageUtils.getFileByAppRoot(c.asText()), PackageUtils::getFileByAppRoot);

        allowedOrigin = getConfigValue("scx.allowed-origin", "*",
                s -> LogUtils.println("✔ 允许的请求源                           \t -->\t " + s, Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.allowed-origin           \t -->\t 已采用默认值 : " + f, Color.RED), JsonNode::asText, (a) -> a);

        dataSourceUrl = getConfigValue("scx.data-source.url");

        dataSourceUsername = getConfigValue("scx.data-source.username");

        dataSourcePassword = CryptoUtils.decryptText(getConfigValue("scx.data-source.password"));

        fixTable = getConfigValue("scx.fix-table", false,
                s -> LogUtils.println("✔ 修复数据表                          \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> LogUtils.println("✘ 未检测到 scx.fix-table               \t -->\t 已采用默认值 : " + f, Color.RED),
                JsonNode::asBoolean, Boolean::valueOf);
    }

}