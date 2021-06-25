package cool.scx.enumeration;

/**
 * AnsiColor 颜色枚举
 * 枚举的顺序目前是按照渐变进行排列的
 *
 * @author 司昌旭
 * @version 0.9.15
 */
public enum AnsiColor {

    /**
     * 亮红色
     */
    BRIGHT_RED(91),

    /**
     * 红色
     */
    RED(31),

    /**
     * 黄色
     */
    YELLOW(33),

    /**
     * 亮黄色
     */
    BRIGHT_YELLOW(93),

    /**
     * 亮绿色
     */
    BRIGHT_GREEN(92),

    /**
     * 绿色
     */
    GREEN(32),

    /**
     * 青色
     */
    CYAN(36),

    /**
     * 蓝色
     */
    BLUE(34),

    /**
     * 亮蓝色
     */
    BRIGHT_BLUE(94),

    /**
     * 亮青色
     */
    BRIGHT_CYAN(96),

    /**
     * 紫色
     */
    MAGENTA(35),

    /**
     * 亮紫色
     */
    BRIGHT_MAGENTA(95),

    /**
     * 黑色
     */
    BLACK(30),

    /**
     * 亮黑色 ( 真的存在这种颜色吗 ? )
     */
    BRIGHT_BLACK(90),

    /**
     * 白色 (其实是有点发灰)
     */
    WHITE(37),

    /**
     * 默认 (一般是白色)
     */
    DEFAULT_COLOR(39),

    /**
     * 亮白色 (这个是真正的白色)
     */
    BRIGHT_WHITE(97);

    /**
     * code 码
     */
    private final int code;

    AnsiColor(int code) {
        this.code = code;
    }

    /**
     * 获取代码
     *
     * @return code 码
     */
    public int code() {
        return code;
    }

}
