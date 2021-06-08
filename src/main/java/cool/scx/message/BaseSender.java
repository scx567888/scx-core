package cool.scx.message;

/**
 * <p>BaseSender interface.</p>
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public interface BaseSender<AddressType, MessageType, Result> {
    /**
     * <p>send.</p>
     *
     * @param address a AddressType object
     * @param message a MessageType object
     */
    Result send(AddressType address, MessageType message);

    /**
     * <p>senderName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String senderName();
}
