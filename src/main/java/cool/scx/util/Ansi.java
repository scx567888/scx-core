package cool.scx.util;

import cool.scx.enumeration.AnsiColor;

import java.util.HashMap;
import java.util.Map;

/**
 * 向控制台打印彩色
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class Ansi {

    /**
     * Constant <code>ANSI</code>
     */
    public static final Ansi OUT = new Ansi();
    private static final Map<Integer, AnsiColor> printColor = new HashMap<>();
    private static int nextPrintColor = 0;
    private static boolean supportAnsiColor = false;

    static {
        var i = 0;
        for (AnsiColor value : AnsiColor.values()) {
            printColor.put(i, value);
            i = i + 1;
        }
        //todo 此处应该检测控制台是否支持彩色
        supportAnsiColor = true;
    }

    private Ansi() {
    }

    /**
     * 向控制台打印指定的颜色
     *
     * @param o         a {@link java.lang.Object} object.
     * @param ansiColor a {@link cool.scx.enumeration.AnsiColor} object.
     */
    private static void print(Object o, AnsiColor ansiColor) {
        if (supportAnsiColor) {
            System.out.print("\u001B[" + ansiColor.code + "m" + o.toString() + "\u001B[0m");
        } else {
            System.out.print(o.toString());
        }
    }

    /**
     * 红色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi red(Object o) {
        print(o, AnsiColor.RED);
        return this;
    }

    /**
     * 绿色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi green(Object o) {
        print(o, AnsiColor.GREEN);
        return this;
    }

    /**
     * 亮青色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightCyan(Object o) {
        print(o, AnsiColor.BRIGHT_CYAN);
        return this;
    }

    /**
     * 蓝色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi blue(Object o) {
        print(o, AnsiColor.BLUE);
        return this;
    }


    /**
     * 青色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi cyan(Object o) {
        print(o, AnsiColor.CYAN);
        return this;
    }

    /**
     * 亮蓝色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightBlue(Object o) {
        print(o, AnsiColor.BRIGHT_BLUE);
        return this;
    }

    /**
     * 亮紫色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightMagenta(Object o) {
        print(o, AnsiColor.BRIGHT_MAGENTA);
        return this;
    }


    /**
     * 亮红色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightRed(Object o) {
        print(o, AnsiColor.BRIGHT_RED);
        return this;
    }

    /**
     * 亮绿色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightGreen(Object o) {
        print(o, AnsiColor.BRIGHT_GREEN);
        return this;
    }

    /**
     * 亮黄色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi brightYellow(Object o) {
        print(o, AnsiColor.BRIGHT_YELLOW);
        return this;
    }

    /**
     * 黄色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi yellow(Object o) {
        print(o, AnsiColor.YELLOW);
        return this;
    }

    /**
     * 紫色
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi magenta(Object o) {
        print(o, AnsiColor.MAGENTA);
        return this;
    }

    /**
     * 换行
     *
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi ln() {
        System.out.println();
        return this;
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

}
