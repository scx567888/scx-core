package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.enumeration.Color;
import cool.scx.util.LogUtils;
import cool.scx.util.PackageUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static cool.scx.config.ScxConfig.getConfigValue;

public class Plugin {
    /**
     * 插件根目录
     */
    public final String root;

    @JsonIgnore
    public final File rootValue;
    /**
     * 关闭的插件名称列表
     */
    public final Set<String> disabledList;

    public Plugin(AtomicBoolean needFixConfig) {
        this.root = getConfigValue("scx.plugin.root", "/plugins/",
                s -> LogUtils.println("✔ 插件根目录                           \t -->\t " + PackageUtils.getFileByAppRoot(s), Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.plugin.root             \t -->\t 已采用默认值 : " + f, Color.RED);
                }, JsonNode::asText, a -> a);
        this.rootValue = PackageUtils.getFileByAppRoot(root);

        this.disabledList = getConfigValue("scx.plugin.disabled-list", new HashSet<String>(),
                s -> LogUtils.println("✔ 禁用插件列表                           \t -->\t " + s, Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.plugin.disabled-list     \t -->\t 已采用默认值 : " + f, Color.RED);
                },
                c -> {
                    var tempSet = new HashSet<String>();
                    c.forEach(cc -> tempSet.add(cc.asText()));
                    return tempSet;
                }, a -> new HashSet<>(Arrays.asList(a.split(","))));
    }
}
