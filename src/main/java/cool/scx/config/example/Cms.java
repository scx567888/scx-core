package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Cms class.</p>
 *
 * @author 司昌旭
 * @version 1.0.10
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

    private Cms() {

    }

    /**
     * <p>Constructor for Cms.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.Cms} object.
     */
    public static Cms from(AtomicBoolean needFixConfig) {
        var cms = new Cms();

        cms.root = ScxConfig.value("scx.cms.root", "/c/",
                s -> Ansi.OUT.green("Y Cms 根目录                         \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.cms.root               \t -->\t 已采用默认值 : " + PackageUtils.getFileByAppRoot(f)).ln();
                });

        cms.rootValue = PackageUtils.getFileByAppRoot(cms.root);

        cms.resourceHttpUrl = ScxConfig.value("scx.cms.resource-http-url", "/static/*",
                s -> Ansi.OUT.green("Y Cms 静态资源 Url                     \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.cms.resource-http-url   \t -->\t 已采用默认值 : " + f).ln();
                });


        cms.resourceLocations = ScxConfig.value("scx.cms.resource-locations", "/c/static",
                s -> Ansi.OUT.green("Y Cms 静态资源目录                     \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.cms.resource-locations  \t -->\t 已采用默认值 : " + PackageUtils.getFileByAppRoot(f)).ln();
                });

        cms.resourceLocationsValue = PackageUtils.getFileByAppRoot(cms.resourceLocations);

        cms.templateSuffix = ScxConfig.value("scx.cms.template-suffix", ".html",
                s -> Ansi.OUT.green("Y Cms 模板文件后缀                     \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.cms.template-suffix    \t -->\t 已采用默认值 : " + f).ln();
                });

        return cms;
    }
}
