package cool.scx.log;

/**
 * 日志级别
 *
 * @author scx567888
 * @version 1.3.0
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
     *
     * @return a {@link java.lang.String} object
     */
    public String toString() {
        return levelStr;
    }

}
