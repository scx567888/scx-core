package cool.scx.base;

import io.vertx.core.buffer.Buffer;

/**
 * <p>BaseVo interface.</p>
 *
 * @author 司昌旭
 * @version 0.5.0
 */
public interface BaseVo {
    /**
     * <p>getString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    Buffer getBuffer();

    /**
     * <p>getContentType.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getContentType();

    /**
     * <p>getContentDisposition.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getContentDisposition();
}
