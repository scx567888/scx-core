package cool.scx.boot;

import cool.scx.enumeration.Color;
import cool.scx.util.StringUtils;

/**
 * <p>ScxBanner class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxBanner {
    static {
        StringUtils.print("   ▄████████ ", Color.RED);
        StringUtils.print(" ▄████████ ", Color.GREEN);
        StringUtils.println("▀████    ▐████▀", Color.BLUE);

        StringUtils.print("  ███    ███ ", Color.RED);
        StringUtils.print("███    ███ ", Color.GREEN);
        StringUtils.println("  ███▌   ████▀", Color.BLUE);

        StringUtils.print("  ███    █▀  ", Color.RED);
        StringUtils.print("███    █▀  ", Color.GREEN);
        StringUtils.println("   ███  ▐███", Color.BLUE);

        StringUtils.print("  ███        ", Color.RED);
        StringUtils.print("███        ", Color.GREEN);
        StringUtils.println("   ▀███▄███▀", Color.BLUE);

        StringUtils.print("▀███████████ ", Color.RED);
        StringUtils.print("███        ", Color.GREEN);
        StringUtils.println("   ████▀██▄", Color.BLUE);

        StringUtils.print("         ███ ", Color.RED);
        StringUtils.print("███    █▄  ", Color.GREEN);
        StringUtils.println("  ▐███  ▀███", Color.BLUE);

        StringUtils.print("   ▄█    ███ ", Color.RED);
        StringUtils.print("███    ███ ", Color.GREEN);
        StringUtils.println(" ▄███     ███▄", Color.BLUE);

        StringUtils.print(" ▄████████▀  ", Color.RED);
        StringUtils.print("████████▀  ", Color.GREEN);
        StringUtils.print("████       ███▄ ", Color.BLUE);

        StringUtils.print(" By ", Color.CYAN);
        StringUtils.println("scx567888@outlook.com", Color.BRIGHT_CYAN);
    }

    /**
     * <p>init.</p>
     */
    public static void init() {

    }
}
