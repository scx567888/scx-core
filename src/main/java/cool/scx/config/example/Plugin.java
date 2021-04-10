package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.util.Ansi;
import cool.scx.util.PackageUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static cool.scx.config.ScxConfig.getConfigValue;

/**
 * <p>Plugin class.</p>
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class Plugin {
    /**
     * 插件根目录
     */
    public String root;

    @JsonIgnore
    public File rootValue;
    /**
     * 关闭的插件名称列表
     */
    public Set<String> disabledList;

    /**
     * <p>Constructor for Plugin.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.Plugin} object.
     */
    public static Plugin from(AtomicBoolean needFixConfig) {
        var plugin = new Plugin();
        plugin.root = getConfigValue("scx.plugin.root", "/plugins/",
                s -> Ansi.OUT.green("Y 插件根目录                           \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.plugin.root             \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asText, a -> a);

        plugin.rootValue = PackageUtils.getFileByAppRoot(plugin.root);

        plugin.disabledList = getConfigValue("scx.plugin.disabled-list", new HashSet<>(),
                s -> Ansi.OUT.green("Y 禁用插件列表                         \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.plugin.disabled-list     \t -->\t 已采用默认值 : " + f).ln();
                },
                c -> {
                    var tempSet = new HashSet<String>();
                    c.forEach(cc -> tempSet.add(cc.asText()));
                    return tempSet;
                }, a -> new HashSet<>(Arrays.asList(a.split(","))));
        return plugin;
    }
}
