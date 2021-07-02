package cool.scx.util;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 简易计时器类 用来计算启动时间
 *
 * @author scx567888
 * @version 1.1.0
 */
public final class Timer {

    private static final HashMap<String, Long> START_TIME_MAP = new HashMap<>();

    /**
     * 启动计时器
     *
     * @param name a {@link java.lang.String} object.
     */
    public static void start(String name) {
        if (START_TIME_MAP.get(name) == null) {
            START_TIME_MAP.put(name, System.nanoTime());
        } else {
            Ansi.OUT.brightRed("定时器 [ " + name + " ] 已存在!!! 若要强制覆盖定时器,请使用 forceStart() !!!");
        }
    }

    /**
     * <p>forceStart.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public static void forceStart(String name) {
        START_TIME_MAP.put(name, System.nanoTime());
    }

    /**
     * 停止计时并返回时间差 单位微毫秒
     *
     * @param name a {@link java.lang.String} object.
     * @return 时间差
     */
    public static long stop(String name) {
        var startTime = START_TIME_MAP.get(name);
        if (startTime == null) {
            return 0;
        } else {
            return System.nanoTime() - startTime;
        }
    }

    /**
     * 停止计时并返回时间差 单位毫秒
     *
     * @param name a {@link java.lang.String} object.
     * @return 时间差
     */
    public static long stopToMillis(String name) {
        return nanosToMillis(stop(name));
    }

    /**
     * 停止计时并返回时间差 单位秒
     *
     * @param name a {@link java.lang.String} object.
     * @return 时间差
     */
    public static double stopToSeconds(String name) {
        return nanosToSeconds(stop(name));
    }

    private static long nanosToMillis(long duration) {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    private static double nanosToSeconds(long duration) {
        return (double) duration / 1.0E9D;
    }

}
