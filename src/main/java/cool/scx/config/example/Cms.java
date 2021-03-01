package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.enumeration.Color;
import cool.scx.util.LogUtils;
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
    public final String root;

    /**
     * cms 根目录 真实值
     */
    @JsonIgnore
    public final File rootValue;

    /**
     * cms 静态资源目录
     */
    public final String resourceHttpUrl;

    /**
     * cms 静态资源 路径 字符串值
     */
    public final String resourceLocations;

    /**
     * cms 静态资源 路径 真实值
     */
    @JsonIgnore
    public final File resourceLocationsValue;
    /**
     * cms 资源后缀
     */
    public final String templateSuffix;
    /**
     * cms Favicon 图标路径 字符串值
     */
    public final String faviconIcoPath;
    @JsonIgnore
    public final File faviconIcoPathValue;

    /**
     * <p>Constructor for Cms.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     */
    public Cms(AtomicBoolean needFixConfig) {

        this.root = getConfigValue("scx.cms.root", "/c/",
                s -> LogUtils.println("✔ Cms 根目录                         \t -->\t " + PackageUtils.getFileByAppRoot(s), Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.cms.root              \t -->\t 已采用默认值 : " + PackageUtils.getFileByAppRoot(f), Color.RED);
                }, JsonNode::asText, a -> a);
        this.rootValue = PackageUtils.getFileByAppRoot(root);

        this.resourceHttpUrl = getConfigValue("scx.cms.resource-http-url", "/static/*",
                s -> LogUtils.println("✔ Cms 静态资源 Url                      \t -->\t " + s, Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.cms.resource-http-url    \t -->\t 已采用默认值 : " + f, Color.RED);
                }, JsonNode::asText, (a) -> a);


        this.resourceLocations = getConfigValue("scx.cms.resource-locations", "/c/static",
                s -> LogUtils.println("✔ Cms 静态资源目录                       \t -->\t " + PackageUtils.getFileByAppRoot(s), Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.cms.resource-locations   \t -->\t 已采用默认值 : " + PackageUtils.getFileByAppRoot(f), Color.RED);
                }, JsonNode::asText, a -> a);

        this.resourceLocationsValue = PackageUtils.getFileByAppRoot(resourceLocations);

        this.templateSuffix = getConfigValue("scx.cms.template-suffix", ".html",
                s -> LogUtils.println("✔ Cms 模板文件后缀                       \t -->\t " + s, Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.cms.template-suffix   \t -->\t 已采用默认值 : " + f, Color.RED);
                }, JsonNode::asText, a -> a);

        this.faviconIcoPath = getConfigValue("scx.cms.favicon-ico-path", "/c/favicon.ico",
                s -> LogUtils.println("✔ Cms Favicon Ico 路径                  \t -->\t " + PackageUtils.getFileByAppRoot(s), Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.cms.favicon-ico-path   \t -->\t 已采用默认值 : " + f, Color.RED);
                }, JsonNode::asText, a -> a);

        this.faviconIcoPathValue = PackageUtils.getFileByAppRoot(faviconIcoPath);
    }
}
