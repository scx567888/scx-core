package cool.scx.base;

/**
 * <p>BaseLogHandler interface.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public interface BaseLogHandler {
    /**
     * <p>recordLog.</p>
     *
     * @param title    a {@link java.lang.String} object.
     * @param content  a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param userIp   a {@link java.lang.String} object.
     * @param type     a {@link java.lang.Integer} object.
     */
    void recordLog(String title, String content, String username, String userIp, Integer type);
}
