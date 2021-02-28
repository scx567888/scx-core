package cool.scx.boot;

import cool.scx.config.ScxConfig;
import cool.scx.enumeration.Color;
import cool.scx.util.LogUtils;

/**
 * <p>ScxBanner class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxBanner {
    static {
        LogUtils.print("   ▄████████ ", Color.RED);
        LogUtils.print(" ▄████████ ", Color.GREEN);
        LogUtils.println("▀████    ▐████▀", Color.BLUE);

        LogUtils.print("  ███    ███ ", Color.RED);
        LogUtils.print("███    ███ ", Color.GREEN);
        LogUtils.println("  ███▌   ████▀", Color.BLUE);

        LogUtils.print("  ███    █▀  ", Color.RED);
        LogUtils.print("███    █▀  ", Color.GREEN);
        LogUtils.println("   ███  ▐███", Color.BLUE);

        LogUtils.print("  ███        ", Color.RED);
        LogUtils.print("███        ", Color.GREEN);
        LogUtils.println("   ▀███▄███▀", Color.BLUE);

        LogUtils.print("▀███████████ ", Color.RED);
        LogUtils.print("███        ", Color.GREEN);
        LogUtils.println("   ████▀██▄", Color.BLUE);

        LogUtils.print("         ███ ", Color.RED);
        LogUtils.print("███    █▄  ", Color.GREEN);
        LogUtils.println("  ▐███  ▀███", Color.BLUE);

        LogUtils.print("   ▄█    ███ ", Color.RED);
        LogUtils.print("███    ███ ", Color.GREEN);
        LogUtils.println(" ▄███     ███▄", Color.BLUE);

        LogUtils.print(" ▄████████▀  ", Color.RED);
        LogUtils.print("████████▀  ", Color.GREEN);
        LogUtils.print("████       ███▄ ", Color.BLUE);

        LogUtils.print(" Version ", Color.CYAN);
        LogUtils.println(ScxConfig.scxVersion(), Color.BRIGHT_CYAN);
    }

    /**
     * <p>init.</p>
     */
    public static void init() {

    }
}
