package cool.scx.boot;

public class ScxTimer {
    private static long startTime;

    public static void timerStart() {
        startTime = System.currentTimeMillis();
    }

    public static long timerStop() {
        return System.currentTimeMillis() - startTime;
    }
}
