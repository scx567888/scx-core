package cool.scx.util;

import cool.scx.enumeration.AnsiColor;

/**
 * 向控制台打印彩色
 *
 * @author scx567888
 * @version 1.0.10
 */
public final class Ansi {

    /**
     * 颜色列表
     */
    private static final AnsiColor[] printColor = AnsiColor.values();

    /**
     * 下一个颜色 做内部索引使用
     */
    private static int nextPrintColor = 0;

    /**
     * 待输出的数据
     */
    private final StringBuilder stringBuilder = new StringBuilder();

    private Ansi() {
    }

    /**
     * <p>out.</p>
     *
     * @return a {@link cool.scx.util.Ansi} object
     */
    public static Ansi out() {
        return new Ansi();
    }

    /**
     * 红色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi red(Object o) {
        add(o, AnsiColor.RED);
        return this;
    }

    /**
     * 绿色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi green(Object o) {
        add(o, AnsiColor.GREEN);
        return this;
    }

    /**
     * 亮青色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi brightCyan(Object o) {
        add(o, AnsiColor.BRIGHT_CYAN);
        return this;
    }

    /**
     * 蓝色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi blue(Object o) {
        add(o, AnsiColor.BLUE);
        return this;
    }

    /**
     * 青色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi cyan(Object o) {
        add(o, AnsiColor.CYAN);
        return this;
    }

    /**
     * 亮蓝色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi brightBlue(Object o) {
        add(o, AnsiColor.BRIGHT_BLUE);
        return this;
    }

    /**
     * 亮紫色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi brightMagenta(Object o) {
        add(o, AnsiColor.BRIGHT_MAGENTA);
        return this;
    }

    /**
     * 亮红色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi brightRed(Object o) {
        add(o, AnsiColor.BRIGHT_RED);
        return this;
    }

    /**
     * 亮绿色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi brightGreen(Object o) {
        add(o, AnsiColor.BRIGHT_GREEN);
        return this;
    }

    /**
     * 黑色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi black(Object o) {
        add(o, AnsiColor.BLACK);
        return this;
    }

    /**
     * 亮黑色 ( 真的存在这种颜色吗 ? )
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi brightBlack(Object o) {
        add(o, AnsiColor.BRIGHT_BLACK);
        return this;
    }

    /**
     * 亮黄色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi brightYellow(Object o) {
        add(o, AnsiColor.BRIGHT_YELLOW);
        return this;
    }

    /**
     * 黄色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi yellow(Object o) {
        add(o, AnsiColor.YELLOW);
        return this;
    }

    /**
     * 紫色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi magenta(Object o) {
        add(o, AnsiColor.MAGENTA);
        return this;
    }

    /**
     * 白色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi white(Object o) {
        add(o, AnsiColor.WHITE);
        return this;
    }

    /**
     * 默认颜色
     *
     * @param o a {@link java.lang.Object} object.
     * @return Ansi 方便链式调用
     */
    public Ansi defaultColor(Object o) {
        add(o, AnsiColor.DEFAULT_COLOR);
        return this;
    }

    /**
     * 换行
     *
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi ln() {
        stringBuilder.append(System.lineSeparator());
        return this;
    }

    /**
     * 向控制台打印输出 颜色根据内部计数器自动读取
     *
     * @param o a {@link java.lang.String} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi color(Object o) {
        if (nextPrintColor >= printColor.length) {
            nextPrintColor = 0;
        }
        add(o, printColor[nextPrintColor]);
        nextPrintColor = nextPrintColor + 1;
        return this;
    }

    private void add(Object o, AnsiColor ansiColor) {
        stringBuilder.append("\u001B[").append(ansiColor.code()).append("m").append(o).append("\u001B[0m");
    }

    /**
     * <p>print.</p>
     */
    public void print() {
        System.out.print(stringBuilder);
    }

    /**
     * <p>println.</p>
     */
    public void println() {
        ln();
        print();
    }

}
