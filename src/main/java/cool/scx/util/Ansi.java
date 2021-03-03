package cool.scx.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Ansi class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public final class Ansi {

    /**
     * Constant <code>ANSI</code>
     */
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

    /**
     * <p>print.</p>
     *
     * @param o         a {@link java.lang.Object} object.
     * @param ansiColor a {@link cool.scx.util.Ansi.AnsiColor} object.
     */
    public static void print(Object o, AnsiColor ansiColor) {
        System.err.print("\u001B[" + ansiColor.toString() + "m" + o.toString() + "\u001B[0m");
    }

    /**
     * <p>red.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi red(Object o) {
        print(o, AnsiColor.RED);
        return this;
    }

    /**
     * <p>green.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi green(Object o) {
        print(o, AnsiColor.GREEN);
        return this;
    }

    /**
     * <p>brightCyan.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightCyan(Object o) {
        print(o, AnsiColor.BRIGHT_CYAN);
        return this;
    }

    /**
     * <p>blue.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi blue(Object o) {
        print(o, AnsiColor.BLUE);
        return this;
    }


    /**
     * <p>cyan.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi cyan(Object o) {
        print(o, AnsiColor.CYAN);
        return this;
    }

    /**
     * <p>brightBlue.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightBlue(Object o) {
        print(o, AnsiColor.BRIGHT_BLUE);
        return this;
    }

    /**
     * <p>brightMagenta.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightMagenta(Object o) {
        print(o, AnsiColor.BRIGHT_MAGENTA);
        return this;
    }


    /**
     * <p>brightRed.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightRed(Object o) {
        print(o, AnsiColor.BRIGHT_RED);
        return this;
    }

    /**
     * <p>brightGreen.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightGreen(Object o) {
        print(o, AnsiColor.BRIGHT_GREEN);
        return this;
    }

    /**
     * <p>brightYellow.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightYellow(Object o) {
        print(o, AnsiColor.BRIGHT_YELLOW);
        return this;
    }

    /**
     * <p>yellow.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi yellow(Object o) {
        print(o, AnsiColor.YELLOW);
        return this;
    }

    /**
     * <p>magenta.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi magenta(Object o) {
        print(o, AnsiColor.MAGENTA);
        return this;
    }

    /**
     * <p>ln.</p>
     */
    public void ln() {
        System.err.println();
    }

    /**
     * 向控制台打印 颜色自动
     *
     * @param o a {@link java.lang.String} object.
     * @return a {@link cool.scx.util.Ansi} object.
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
