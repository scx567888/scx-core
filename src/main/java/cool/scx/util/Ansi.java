package cool.scx.util;

import cool.scx.boot.ScxParameters;
import cool.scx.enumeration.AnsiColor;

/**
 * 向控制台打印彩色
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class Ansi {

    /**
     * Ansi 实例
     */
    public static final Ansi OUT = new Ansi();

    /**
     * 颜色列表
     */
    private static final AnsiColor[] printColor = AnsiColor.values();

    /**
     * 是否支持彩色打印 todo 此处的检测机制需要重新研究
     */
    private static final boolean supportAnsiColor = isSupportAnsiColor();

    /**
     * 下一个颜色 做内部索引使用
     */
    private static int nextPrintColor = 0;

    private Ansi() {
    }

    /**
     * 向控制台打印指定的颜色
     *
     * @param o         要打印的语句
     * @param ansiColor 颜色枚举
     */
    private static void print(Object o, AnsiColor ansiColor) {
        if (supportAnsiColor) {
            System.out.print("\u001B[" + ansiColor.code + "m" + o.toString() + "\u001B[0m");
        } else {
            System.out.print(o.toString());
        }
    }

    /**
     * 判断当前环境是否支持彩色打印 以避免输出无用的拼接转义符
     * 但此方法 获取方式过于局限 需要修改
     *
     * @return 是否支持
     */
    private static boolean isSupportAnsiColor() {
        for (String parameter : ScxParameters.parameters()) {
            if (parameter.startsWith("--supportAnsiColor=")) {
                String[] split = parameter.split("=");
                if (split.length == 2) {
                    return "true".equals(split[1]);
                }
            }
        }
        return true;
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
     * 向控制台打印输出 颜色根据内部计数器自动读取
     *
     * @param o a {@link java.lang.String} object.
     * @return a {@link cool.scx.util.Ansi} object.
     */
    public Ansi print(Object o) {
        if (nextPrintColor >= printColor.length) {
            nextPrintColor = 0;
        }
        print(o, printColor[nextPrintColor]);
        nextPrintColor = nextPrintColor + 1;
        return this;
    }

}
