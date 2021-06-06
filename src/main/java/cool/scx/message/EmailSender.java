package cool.scx.message;

/**
 * <p>EmailSender class.</p>
 *
 * @author 司昌旭
 * @version 1.1.9
 */
public class EmailSender implements BaseSender<String, String> {


    /**
     * <p>Constructor for EmailSender.</p>
     */
    public EmailSender() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(String address, String message) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String senderName() {
        return "email";
    }
}
