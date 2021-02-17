package cool.scx.enumeration;

/**
 * <p>Color class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public enum Color {

    DEFAULT("39"),

    BLACK("30"),

    RED("31"),

    GREEN("32"),

    YELLOW("33"),

    BLUE("34"),

    MAGENTA("35"),

    CYAN("36"),

    WHITE("37"),

    BRIGHT_BLACK("90"),

    BRIGHT_RED("91"),

    BRIGHT_GREEN("92"),

    BRIGHT_YELLOW("93"),

    BRIGHT_BLUE("94"),

    BRIGHT_MAGENTA("95"),

    BRIGHT_CYAN("96"),

    BRIGHT_WHITE("97");

    private final String code;

    Color(String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public String toString() {
        return this.code;
    }

}