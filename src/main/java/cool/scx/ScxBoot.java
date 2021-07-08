package cool.scx;

import cool.scx.auth.LoginItem;
import cool.scx.auth.ScxAuth;
import cool.scx.config.ScxConfig;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;

import java.io.*;

/**
 * ScxBoot 启动工具类
 *
 * @author scx567888
 * @version 1.1.9
 */
final class ScxBoot {

    /**
     * SESSION_CACHE 存储路径 默认为 AppRoot 下的  scx-session.cache 文件
     */
    private static final String SESSION_CACHE_PATH = "AppRoot:scx-session.cache";

    /**
     * 在控制台上打印 banner
     */
    static void showBanner() {
        Ansi.OUT.red("   ▄████████ ").green(" ▄████████ ").blue("▀████    ▐████▀ ").ln();
        Ansi.OUT.red("  ███    ███ ").green("███    ███ ").blue("  ███▌   ████▀  ").ln();
        Ansi.OUT.red("  ███    █▀  ").green("███    █▀  ").blue("   ███  ▐███    ").ln();
        Ansi.OUT.red("  ███        ").green("███        ").blue("   ▀███▄███▀    ").ln();
        Ansi.OUT.red("▀███████████ ").green("███        ").blue("   ████▀██▄     ").ln();
        Ansi.OUT.red("         ███ ").green("███    █▄  ").blue("  ▐███  ▀███    ").ln();
        Ansi.OUT.red("   ▄█    ███ ").green("███    ███ ").blue(" ▄███     ███▄  ").ln();
        Ansi.OUT.red(" ▄████████▀  ").green("████████▀  ").blue("████       ███▄ ").cyan(" Version ").brightCyan(ScxConfig.SCX_VERSION).ln();
    }

    /**
     * 添加监听事件
     * 目前只监听项目停止事件
     */
    static void addListener() {
        var sessionCache = FileUtils.getFileByAppRoot(SESSION_CACHE_PATH);
        try (var f = new FileInputStream(sessionCache); var o = new ObjectInputStream(f)) {
            var loginItems = (LoginItem[]) o.readObject();
            for (LoginItem loginItem : loginItems) {
                ScxAuth.addLoginItem(loginItem);
            }
            Ansi.OUT.brightGreen("成功从 SessionCache 中恢复 " + ScxAuth.getAllLoginItem().size() + " 条数据!!!").ln();
        } catch (Exception ignored) {

        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (var f = new FileOutputStream(sessionCache); var o = new ObjectOutputStream(f)) {
                // 执行模块的 stop 生命周期
                ScxModuleHandler.stopModules();
                Ansi.OUT.red("项目正在停止!!!").ln();
                Ansi.OUT.red("保存 Session 中!!!").ln();
                o.writeObject(ScxAuth.getAllLoginItem().toArray(new LoginItem[0]));
            } catch (IOException ignored) {

            }
        }));
    }

}
