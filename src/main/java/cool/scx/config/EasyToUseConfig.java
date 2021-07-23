package cool.scx.config;

import cool.scx.enumeration.LogOutType;
import cool.scx.util.*;
import org.slf4j.event.Level;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

class EasyToUseConfig {

    /**
     * 端口号
     */
    final int port;

    /**
     * 允许的 域
     */
    final String allowedOrigin;

    /**
     * 是否显示 log
     */
    final boolean showLog;

    /**
     * 是否真实删除
     */
    final boolean realDelete;

    /**
     * 关闭的插件名称列表
     */
    final Set<String> disabledPluginList;


    /**
     * 是否开启 https
     */
    final boolean isOpenHttps;

    /**
     * ssl 证书路径 字符串值
     */
    final File sslPath;

    /**
     * ssl 证书密码 (解密后)
     */
    final String sslPassword;

    /**
     * 模板 根目录 字符串值
     */
    final File templateRoot;

    /**
     * 模板 静态资源目录
     */
    final String templateResourceHttpUrl;

    /**
     * 模板 静态资源 路径 真实值
     */

    final File templateResourceRoot;

    /**
     * 数据源地址
     */
    final String dataSourceHost;

    /**
     * 数据源端口
     */
    final Integer dataSourcePort;

    /**
     * 数据源端口
     */
    final String dataSourceDatabase;

    /**
     * 其他连接参数
     */
    final Set<String> dataSourceParameters;

    /**
     * 数据源 用户名
     */
    final String dataSourceUsername;

    /**
     * 数据源密码 真实值(解密后)
     */

    final String dataSourcePassword;

    final LogOutType logOutType;

    final File logFilePath;

    final Level rootLevel;

    final Map<String, Level> logLevelMapping;

    private final Map<Consumer<Object>, Object> logInfo = new LinkedHashMap<>();

    EasyToUseConfig() {

        port = getWithInfo("scx.port", 8080,
                s -> {
                    Ansi.out().green("Y 服务器 IP 地址                       \t -->\t " + NetUtils.getLocalAddress()).println();
                    Ansi.out().green("Y 端口号                               \t -->\t " + s).println();
                },
                f -> Ansi.out().red("N 未检测到 scx.port                   \t -->\t 已采用默认值 : " + f).println());

        showLog = getWithInfo("scx.show-log", true,
                s -> Ansi.out().green("Y 是否打印日志                         \t -->\t " + (s ? "是" : "否")).println(),
                f -> Ansi.out().red("N 未检测到 scx.show-log               \t -->\t 已采用默认值 : " + f).println());

        realDelete = getWithInfo("scx.real-delete", false,
                s -> Ansi.out().green("Y 数据库删除方式为                     \t -->\t " + (s ? "物理删除" : "逻辑删除")).println(),
                f -> Ansi.out().red("N 未检测到 scx.real-delete            \t -->\t 已采用默认值 : " + f).println());

        allowedOrigin = getWithInfo("scx.allowed-origin", "*",
                s -> Ansi.out().green("Y 允许的请求源                         \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.allowed-origin          \t -->\t 已采用默认值 : " + f).println());

        var tempDisabledPluginList = getWithInfo("scx.disabled-plugins.disabled-plugin", new ArrayList<String>(),
                s -> Ansi.out().green("Y 禁用插件列表                         \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.disabled-plugins        \t -->\t 已采用默认值 : " + f).println());

        disabledPluginList = new HashSet<>(tempDisabledPluginList);

        String tempTemplateRoot = getWithInfo("scx.template.root", "AppRoot:/c/",
                s -> Ansi.out().green("Y 模板根目录                           \t -->\t " + FileUtils.getFileByAppRoot(s)).println(),
                f -> Ansi.out().red("N 未检测到 scx.template.root           \t -->\t 已采用默认值 : " + FileUtils.getFileByAppRoot(f)).println());

        templateRoot = FileUtils.getFileByAppRoot(tempTemplateRoot);

        templateResourceHttpUrl = getWithInfo("scx.template.resource-http-url", "/static/*",
                s -> Ansi.out().green("Y 模板静态资源 Url                     \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.template.resource-http-url\t -->\t 已采用默认值 : " + f).println());

        String tempTemplateResourceRoot = getWithInfo("scx.template.resource-root", "AppRoot:/c/static",
                s -> Ansi.out().green("Y 模板静态资源目录                     \t -->\t " + FileUtils.getFileByAppRoot(s)).println(),
                f -> Ansi.out().red("N 未检测到 scx.template.resource-root   \t -->\t 已采用默认值 : " + FileUtils.getFileByAppRoot(f)).println());

        templateResourceRoot = FileUtils.getFileByAppRoot(tempTemplateResourceRoot);

        isOpenHttps = getWithInfo("scx.https.is-open", false,
                s -> Ansi.out().green("Y 是否开启 https                       \t -->\t " + (s ? "是" : "否")).println(),
                f -> Ansi.out().red("N 未检测到 scx.https.is-open           \t -->\t 已采用默认值 : " + f).println());

        var tempSSLPath = getWithInfo("scx.https.ssl-path", "",
                s -> Ansi.out().green("Y 证书路径                            \t -->\t " + FileUtils.getFileByAppRoot(s)).println(),
                f -> Ansi.out().red("N 未检测到 scx.https.ssl-path         \t -->\t 请检查证书路径是否正确").println());

        sslPath = FileUtils.getFileByAppRoot(tempSSLPath);

        var tempSSLPassword = "";

        if (isOpenHttps) {
            try {
                tempSSLPassword = CryptoUtils.decryptText(getWithInfo("scx.https.ssl-password", "",
                        Tidy::NoCode,
                        f -> Ansi.out().red("N 未检测到 scx.https.ssl-password      \t -->\t 请检查证书密码是否正确").println()));
            } catch (Exception e) {
                Ansi.out().red("N 解密 scx.https.ssl-password  出错        \t -->\t 请检查证书密码是否正确").println();
            }
        }

        sslPassword = tempSSLPassword;

        dataSourceHost = getWithInfo("scx.data-source.host", "127.0.0.1",
                s -> Ansi.out().green("Y 数据源 Host                          \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.data-source.host        \t -->\t 已采用默认值 : " + f).println());

        dataSourcePort = getWithInfo("scx.data-source.port", 3306,
                s -> Ansi.out().green("Y 数据源 端口号                        \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.data-source.port        \t -->\t 已采用默认值 : " + f).println());

        dataSourceDatabase = getWithInfo("scx.data-source.database", "scx",
                s -> Ansi.out().green("Y 数据源 数据库名称                    \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.data-source.database    \t -->\t 已采用默认值 : " + f).println());

        dataSourceUsername = getWithInfo("scx.data-source.username", "root",
                s -> Ansi.out().green("Y 数据源 用户名                        \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.data-source.username    \t -->\t 已采用默认值 : " + f).println());

        String tempDataSourcePassword = "";

        try {
            tempDataSourcePassword = CryptoUtils.decryptText(getWithInfo("scx.data-source.password", "",
                    s -> Ansi.out().green("Y 数据源 连接密码                      \t -->\t " + s).println(),
                    f -> Ansi.out().red("N 未检测到 scx.data-source.password    \t -->\t 请检查数据库密码是否正确").println()));
        } catch (Exception e) {
            Ansi.out().red("N 解密 scx.data-source.password 出错  \t -->\t 请检查数据库密码是否正确").println();
        }

        dataSourcePassword = tempDataSourcePassword;

        var tempDataSourceParameters = getWithInfo("scx.data-source.parameters.parameter", new ArrayList<String>(),
                s -> Ansi.out().green("Y 数据源 连接参数                      \t -->\t " + s).println(),
                f -> Ansi.out().red("N 未检测到 scx.data-source.parameters  \t -->\t 已采用默认值 : " + f).println());

        dataSourceParameters = new HashSet<>(tempDataSourceParameters);

        logOutType = LogOutType.by("BOTH");
        logFilePath = new File("");
        rootLevel = Level.WARN;
        logLevelMapping = new HashMap<>();

        String rowsStrings[] = new String[]{"1",
                "1234",
                "司昌旭",
                "123456789"};

        String column1Format = "%-20s";    // 至少3个字符，左对齐

        String column3Format = "%6s";   // 固定大小6个字符，右对齐

        String formatInfo = column1Format + " - " + column3Format;

        for (int i = 0; i < 3; i++) {
            System.out.format(formatInfo, rowsStrings[i], rowsStrings[i]);

            System.out.println();
        }

    }

    @SuppressWarnings("unchecked")
    public <T> T getWithInfo(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun) {
        T o = ScxConfig.getOrDefault(keyPath, defaultVal);
        if (o == null) {
            logInfo.put((Consumer<Object>) failFun, defaultVal);
//            logInfo.add((n) -> Ansi.out().red("N 未检测到 " + keyPath + "        \t -->\t 已采用默认值 : " + defaultVal).println());
            return defaultVal;
        } else {
            logInfo.put((Consumer<Object>) successFun, o);
            return o;
        }
    }

    /**
     * 打印配置文件内容
     */
    public void logConfig() {
        logInfo.forEach(Consumer::accept);
    }

}
