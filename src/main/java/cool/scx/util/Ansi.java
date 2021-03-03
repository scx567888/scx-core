package cool.scx.util;

import java.util.HashMap;
import java.util.Map;

public final class Ansi {

    public static final Ansi ANSI = new Ansi();
    private static final Map<Integer, AnsiColor> printColor = new HashMap<>();
    private static int nextPrintColor = 0;

    static {
        var i = 0;
        for (AnsiColor value : AnsiColor.values()) {
            printColor.put(i, value);
            i = i + 1;
        }
    }

    private Ansi() {
    }

    public static void print(Object o, AnsiColor ansiColor) {
        System.err.print("\u001B[" + ansiColor.toString() + "m" + o.toString() + "\u001B[0m");
    }

    public Ansi red(Object o) {
        print(o, AnsiColor.RED);
        return this;
    }

    public Ansi green(Object o) {
        print(o, AnsiColor.GREEN);
        return this;
    }

    public Ansi brightCyan(Object o) {
        print(o, AnsiColor.BRIGHT_CYAN);
        return this;
    }

    public Ansi blue(Object o) {
        print(o, AnsiColor.BLUE);
        return this;
    }


    public Ansi cyan(Object o) {
        print(o, AnsiColor.CYAN);
        return this;
    }

    public Ansi brightBlue(Object o) {
        print(o, AnsiColor.BRIGHT_BLUE);
        return this;
    }

    public Ansi brightMagenta(Object o) {
        print(o, AnsiColor.BRIGHT_MAGENTA);
        return this;
    }


    public Ansi brightRed(Object o) {
        print(o, AnsiColor.BRIGHT_RED);
        return this;
    }

    public Ansi brightGreen(Object o) {
        print(o, AnsiColor.BRIGHT_GREEN);
        return this;
    }

    public Ansi brightYellow(Object o) {
        print(o, AnsiColor.BRIGHT_YELLOW);
        return this;
    }

    public Ansi yellow(Object o) {
        print(o, AnsiColor.YELLOW);
        return this;
    }

    public Ansi magenta(Object o) {
        print(o, AnsiColor.MAGENTA);
        return this;
    }

    public void ln() {
        System.err.println();
    }

    /**
     * 向控制台打印 颜色自动
     *
     * @param o a {@link java.lang.String} object.
     */
    public Ansi print(Object o) {
        if (nextPrintColor >= printColor.size()) {
            nextPrintColor = 0;
        }
        print(o, printColor.get(nextPrintColor));
        nextPrintColor = nextPrintColor + 1;
        return this;
    }

    private enum AnsiColor {

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

        AnsiColor(String code) {
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

}
