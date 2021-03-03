package cool.scx.boot;

import cool.scx.config.ScxConfig;
import cool.scx.util.log.Color;

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
    public static void show() {
        print("   ▄████████ ", Color.RED);
        print(" ▄████████ ", Color.GREEN);
        println("▀████    ▐████▀", Color.BLUE);

        print("  ███    ███ ", Color.RED);
        print("███    ███ ", Color.GREEN);
        println("  ███▌   ████▀", Color.BLUE);

        print("  ███    █▀  ", Color.RED);
        print("███    █▀  ", Color.GREEN);
        println("   ███  ▐███", Color.BLUE);

        print("  ███        ", Color.RED);
        print("███        ", Color.GREEN);
        println("   ▀███▄███▀", Color.BLUE);

        print("▀███████████ ", Color.RED);
        print("███        ", Color.GREEN);
        println("   ████▀██▄", Color.BLUE);

        print("         ███ ", Color.RED);
        print("███    █▄  ", Color.GREEN);
        println("  ▐███  ▀███", Color.BLUE);

        print("   ▄█    ███ ", Color.RED);
        print("███    ███ ", Color.GREEN);
        println(" ▄███     ███▄", Color.BLUE);

        print(" ▄████████▀  ", Color.RED);
        print("████████▀  ", Color.GREEN);
        print("████       ███▄ ", Color.BLUE);

        print(" Version ", Color.CYAN);
        println(ScxConfig.scxVersion(), Color.BRIGHT_CYAN);
    }

    /**
     * 输出日志 不换行
     *
     * @param str       a {@link java.lang.String} object.
     * @param ansiColor a {@link Color} object.
     */
    private static void print(String str, Color ansiColor) {
        System.err.print("\u001B[" + ansiColor.toString() + "m" + str + "\u001B[0m");
    }

    /**
     * 输出日志
     *
     * @param str       a {@link java.lang.String} object.
     * @param ansiColor a {@link Color} object.
     */
    private static void println(String str, Color ansiColor) {
        System.err.println("\u001B[" + ansiColor.toString() + "m" + str + "\u001B[0m");
    }
}
