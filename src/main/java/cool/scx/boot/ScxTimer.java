package cool.scx.boot;

/**
 * <p>ScxTimer class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class ScxTimer {
    private static long startTime;

    /**
     * <p>timerStart.</p>
     */
    public static void timerStart() {
        startTime = System.currentTimeMillis();
    }

    /**
     * <p>timerStop.</p>
     *
     * @return a long.
     */
    public static long timerStop() {
        return System.currentTimeMillis() - startTime;
    }
}
