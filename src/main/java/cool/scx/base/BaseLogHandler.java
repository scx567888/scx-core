package cool.scx.base;

import cool.scx.annotation.MustHaveImpl;

/**
 * <p>BaseLogHandler interface.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@MustHaveImpl
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
