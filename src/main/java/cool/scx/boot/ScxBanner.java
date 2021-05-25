package cool.scx.boot;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;

/**
 * scxBanner
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxBanner {

    /**
     * 在控制台上打印 banner
     */
    private static void show() {
        Ansi.OUT.red("   ▄████████ ").green(" ▄████████ ").blue("▀████    ▐████▀ ").ln();
        Ansi.OUT.red("  ███    ███ ").green("███    ███ ").blue("  ███▌   ████▀  ").ln();
        Ansi.OUT.red("  ███    █▀  ").green("███    █▀  ").blue("   ███  ▐███    ").ln();
        Ansi.OUT.red("  ███        ").green("███        ").blue("   ▀███▄███▀    ").ln();
        Ansi.OUT.red("▀███████████ ").green("███        ").blue("   ████▀██▄     ").ln();
        Ansi.OUT.red("         ███ ").green("███    █▄  ").blue("  ▐███  ▀███    ").ln();
        Ansi.OUT.red("   ▄█    ███ ").green("███    ███ ").blue(" ▄███     ███▄  ").ln();
        Ansi.OUT.red(" ▄████████▀  ").green("████████▀  ").blue("████       ███▄ ").cyan(" Version ").brightCyan(ScxConfig.scxVersion()).ln();
    }

    public static void initBanner() {
        show();
    }
}
