package cool.scx.enumeration;

public enum AnsiColor {

    BRIGHT_RED(91),
    DEFAULT(39),
    RED(31),
    YELLOW(33),
    BRIGHT_YELLOW(93),
    BRIGHT_GREEN(92),
    GREEN(32),
    CYAN(36),
    BLUE(34),
    BRIGHT_BLUE(94),
    BRIGHT_CYAN(96),
    MAGENTA(35),
    BRIGHT_MAGENTA(95),
    BLACK(30),
    BRIGHT_BLACK(90),
    WHITE(37),
    BRIGHT_WHITE(97);

    public final int code;

    AnsiColor(int code) {
        this.code = code;
    }

}
