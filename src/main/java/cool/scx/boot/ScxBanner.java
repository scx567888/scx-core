package cool.scx.boot;

import cool.scx.util.StringUtils;

public final class ScxBanner {
    static {
        StringUtils.print("   ▄████████ ", StringUtils.Color.RED);
        StringUtils.print(" ▄████████ ", StringUtils.Color.GREEN);
        StringUtils.println("▀████    ▐████▀", StringUtils.Color.BLUE);

        StringUtils.print("  ███    ███ ", StringUtils.Color.RED);
        StringUtils.print("███    ███ ", StringUtils.Color.GREEN);
        StringUtils.println("  ███▌   ████▀", StringUtils.Color.BLUE);

        StringUtils.print("  ███    █▀  ", StringUtils.Color.RED);
        StringUtils.print("███    █▀  ", StringUtils.Color.GREEN);
        StringUtils.println("   ███  ▐███", StringUtils.Color.BLUE);

        StringUtils.print("  ███        ", StringUtils.Color.RED);
        StringUtils.print("███        ", StringUtils.Color.GREEN);
        StringUtils.println("   ▀███▄███▀", StringUtils.Color.BLUE);

        StringUtils.print("▀███████████ ", StringUtils.Color.RED);
        StringUtils.print("███        ", StringUtils.Color.GREEN);
        StringUtils.println("   ████▀██▄", StringUtils.Color.BLUE);

        StringUtils.print("         ███ ", StringUtils.Color.RED);
        StringUtils.print("███    █▄  ", StringUtils.Color.GREEN);
        StringUtils.println("  ▐███  ▀███", StringUtils.Color.BLUE);

        StringUtils.print("   ▄█    ███ ", StringUtils.Color.RED);
        StringUtils.print("███    ███ ", StringUtils.Color.GREEN);
        StringUtils.println(" ▄███     ███▄", StringUtils.Color.BLUE);

        StringUtils.print(" ▄████████▀  ", StringUtils.Color.RED);
        StringUtils.print("████████▀  ", StringUtils.Color.GREEN);
        StringUtils.print("████       ███▄ ", StringUtils.Color.BLUE);

        StringUtils.print(" By ", StringUtils.Color.CYAN);
        StringUtils.println("scx567888@outlook.com", StringUtils.Color.BRIGHT_CYAN);
    }

    public static void init() {

    }
}
