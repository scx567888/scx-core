package cool.scx.boot;

import cool.scx.enumeration.Color;
import cool.scx.util.StringUtils;

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

    public static void init() {

    }
}
