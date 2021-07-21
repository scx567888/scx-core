package cool.scx.log;

/**
 * 日志级别
 */
public enum ScxLogLevel {

    ERROR("ERROR"),
    WARN("WARN"),
    INFO("INFO"),
    DEBUG("DEBUG"),
    TRACE("TRACE");

    private String levelStr;

    ScxLogLevel(String s) {
        levelStr = s;
    }

    /**
     * Returns the string representation of this Level.
     */
    public String toString() {
        return levelStr;
    }

}
