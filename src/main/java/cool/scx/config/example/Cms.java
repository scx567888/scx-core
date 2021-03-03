package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static cool.scx.config.ScxConfig.getConfigValue;

/**
 * <p>Cms class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class Cms {
    /**
     * cms 根目录 字符串值
     */
    public String root;

    /**
     * cms 根目录 真实值
     */
    @JsonIgnore
    public File rootValue;

    /**
     * cms 静态资源目录
     */
    public String resourceHttpUrl;

    /**
     * cms 静态资源 路径 字符串值
     */
    public String resourceLocations;

    /**
     * cms 静态资源 路径 真实值
     */
    @JsonIgnore
    public File resourceLocationsValue;
    /**
     * cms 资源后缀
     */
    public String templateSuffix;
    /**
     * cms Favicon 图标路径 字符串值
     */
    public String faviconIcoPath;
    @JsonIgnore
    public File faviconIcoPathValue;

    /**
     * <p>Constructor for Cms.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.Cms} object.
     */
    public static Cms from(AtomicBoolean needFixConfig) {
        var cms = new Cms();

        cms.root = getConfigValue("scx.cms.root", "/c/",
                s -> Ansi.ANSI.green("✔ Cms 根目录                         \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.cms.root              \t -->\t 已采用默认值 : " + PackageUtils.getFileByAppRoot(f)).ln();
                }, JsonNode::asText, a -> a);

        cms.rootValue = PackageUtils.getFileByAppRoot(cms.root);

        cms.resourceHttpUrl = getConfigValue("scx.cms.resource-http-url", "/static/*",
                s -> Ansi.ANSI.green("✔ Cms 静态资源 Url                      \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.cms.resource-http-url    \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asText, (a) -> a);


        cms.resourceLocations = getConfigValue("scx.cms.resource-locations", "/c/static",
                s -> Ansi.ANSI.green("✔ Cms 静态资源目录                       \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.cms.resource-locations   \t -->\t 已采用默认值 : " + PackageUtils.getFileByAppRoot(f)).ln();
                }, JsonNode::asText, a -> a);

        cms.resourceLocationsValue = PackageUtils.getFileByAppRoot(cms.resourceLocations);

        cms.templateSuffix = getConfigValue("scx.cms.template-suffix", ".html",
                s -> Ansi.ANSI.green("✔ Cms 模板文件后缀                       \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.cms.template-suffix   \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asText, a -> a);

        cms.faviconIcoPath = getConfigValue("scx.cms.favicon-ico-path", "/c/favicon.ico",
                s -> Ansi.ANSI.green("✔ Cms Favicon Ico 路径                  \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.cms.favicon-ico-path   \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asText, a -> a);

        cms.faviconIcoPathValue = PackageUtils.getFileByAppRoot(cms.faviconIcoPath);
        return cms;
    }
}
