package cool.scx.boot;

import java.util.HashMap;

/**
 * 计时器类 用来计算启动时间
 *
 * @author 司昌旭
 * @version 1.1.0
 */
public final class ScxTimer {

    private static final HashMap<String, Long> START_TIME_MAP = new HashMap<>();

    /**
     * 启动计时器
     */
    public static void timerStart(String name) {
        START_TIME_MAP.put(name, System.currentTimeMillis());
    }

    /**
     * 停止计时并返回时间差
     *
     * @return 时间差
     */
    public static long timerStop(String name) {
        var startTime = START_TIME_MAP.get(name);
        if (startTime == null) {
            return -1;
        } else {
            return System.currentTimeMillis() - startTime;
        }
    }

}
