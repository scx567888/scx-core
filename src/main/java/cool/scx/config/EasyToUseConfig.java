package cool.scx.config;

import cool.scx.util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
     * cms 根目录 字符串值
     */
    final File cmsRoot;

    /**
     * cms 静态资源目录
     */
    final String cmsResourceHttpUrl;

    /**
     * cms 静态资源 路径 真实值
     */

    final File cmsResourceLocations;
    /**
     * cms 资源后缀
     */
    final String cmsTemplateSuffix;

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

    EasyToUseConfig() {

        port = ScxConfig.get("scx.port", 8080,
                s -> {
                    Ansi.OUT.green("Y 服务器 IP 地址                       \t -->\t " + NetUtils.getLocalAddress()).ln();
                    Ansi.OUT.green("Y 端口号                               \t -->\t " + s).ln();
                },
                f -> Ansi.OUT.red("N 未检测到 scx.port                   \t -->\t 已采用默认值 : " + f).ln());

        showLog = ScxConfig.get("scx.show-log", true,
                s -> Ansi.OUT.green("Y 是否打印日志                         \t -->\t " + (s ? "是" : "否")).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.show-log               \t -->\t 已采用默认值 : " + f).ln());

        realDelete = ScxConfig.get("scx.real-delete", false,
                s -> Ansi.OUT.green("Y 数据库删除方式为                     \t -->\t " + (s ? "物理删除" : "逻辑删除")).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.real-delete            \t -->\t 已采用默认值 : " + f).ln());

        allowedOrigin = ScxConfig.get("scx.allowed-origin", "*",
                s -> Ansi.OUT.green("Y 允许的请求源                         \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.allowed-origin          \t -->\t 已采用默认值 : " + f).ln());

        var tempDisabledPluginList = ScxConfig.get("scx.plugin-disabled-list", new ArrayList<String>(),
                s -> Ansi.OUT.green("Y 禁用插件列表                         \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.plugin-disabled-list    \t -->\t 已采用默认值 : " + f).ln());

        disabledPluginList = new HashSet<>(tempDisabledPluginList);

        String tempCmsRoot = ScxConfig.get("scx.cms.root", "AppRoot:/c/",
                s -> Ansi.OUT.green("Y Cms 根目录                         \t -->\t " + FileUtils.getFileByAppRoot(s)).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.cms.root               \t -->\t 已采用默认值 : " + FileUtils.getFileByAppRoot(f)).ln());

        cmsRoot = FileUtils.getFileByAppRoot(tempCmsRoot);

        cmsResourceHttpUrl = ScxConfig.get("scx.cms.resource-http-url", "/static/*",
                s -> Ansi.OUT.green("Y Cms 静态资源 Url                     \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.cms.resource-http-url   \t -->\t 已采用默认值 : " + f).ln());

        String tempCmsResourceLocations = ScxConfig.get("scx.cms.resource-locations", "AppRoot:/c/static",
                s -> Ansi.OUT.green("Y Cms 静态资源目录                     \t -->\t " + FileUtils.getFileByAppRoot(s)).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.cms.resource-locations  \t -->\t 已采用默认值 : " + FileUtils.getFileByAppRoot(f)).ln());

        cmsResourceLocations = FileUtils.getFileByAppRoot(tempCmsResourceLocations);

        cmsTemplateSuffix = ScxConfig.get("scx.cms.template-suffix", ".html",
                s -> Ansi.OUT.green("Y Cms 模板文件后缀                     \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.cms.template-suffix    \t -->\t 已采用默认值 : " + f).ln());

        isOpenHttps = ScxConfig.get("scx.https.is-open", false,
                s -> Ansi.OUT.green("Y 是否开启 https                       \t -->\t " + (s ? "是" : "否")).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.https.is-open           \t -->\t 已采用默认值 : " + f).ln());

        var tempSSLPath = ScxConfig.get("scx.https.ssl-path", "",
                s -> Ansi.OUT.green("Y 证书路径                            \t -->\t " + FileUtils.getFileByAppRoot(s)).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.https.ssl-path         \t -->\t 请检查证书路径是否正确").ln());

        sslPath = FileUtils.getFileByAppRoot(tempSSLPath);

        var tempSSLPassword = "";

        if (isOpenHttps) {
            try {
                tempSSLPassword = CryptoUtils.decryptText(ScxConfig.get("scx.https.ssl-password", "",
                        Tidy::NoCode,
                        f -> Ansi.OUT.red("N 未检测到 scx.https.ssl-password      \t -->\t 请检查证书密码是否正确").ln()));
            } catch (Exception e) {
                Ansi.OUT.red("N 解密 scx.https.ssl-password  出错        \t -->\t 请检查证书密码是否正确").ln();
            }
        }

        sslPassword = tempSSLPassword;

        dataSourceHost = ScxConfig.get("scx.data-source.host", "127.0.0.1",
                s -> Ansi.OUT.green("Y 数据源 Host                          \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.data-source.host        \t -->\t 已采用默认值 : " + f).ln());

        dataSourcePort = ScxConfig.get("scx.data-source.port", 3306,
                s -> Ansi.OUT.green("Y 数据源 端口号                        \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.data-source.port        \t -->\t 已采用默认值 : " + f).ln());

        dataSourceDatabase = ScxConfig.get("scx.data-source.database", "scx",
                s -> Ansi.OUT.green("Y 数据源 数据库名称                    \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.data-source.database    \t -->\t 已采用默认值 : " + f).ln());

        dataSourceUsername = ScxConfig.get("scx.data-source.username", "root",
                s -> Ansi.OUT.green("Y 数据源 用户名                        \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.data-source.username    \t -->\t 已采用默认值 : " + f).ln());

        String tempDataSourcePassword = "";

        try {
            tempDataSourcePassword = CryptoUtils.decryptText(ScxConfig.get("scx.data-source.password", "",
                    s -> Ansi.OUT.green("Y 数据源 连接密码                      \t -->\t " + s).ln(),
                    f -> Ansi.OUT.red("N 未检测到 scx.data-source.password    \t -->\t 请检查数据库密码是否正确").ln()));
        } catch (Exception e) {
            Ansi.OUT.red("N 解密 scx.data-source.password 出错  \t -->\t 请检查数据库密码是否正确").ln();
        }

        dataSourcePassword = tempDataSourcePassword;

        var tempDataSourceParameters = ScxConfig.get("scx.data-source.parameters", new ArrayList<String>(),
                s -> Ansi.OUT.green("Y 数据源 连接参数                      \t -->\t " + s).ln(),
                f -> Ansi.OUT.red("N 未检测到 scx.data-source.parameters  \t -->\t 已采用默认值 : " + f).ln());

        dataSourceParameters = new HashSet<>(tempDataSourceParameters);

    }

}
