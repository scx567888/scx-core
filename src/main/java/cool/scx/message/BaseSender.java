package cool.scx.message;

public interface BaseSender<AddressType, MessageType> {
    void send(AddressType address, MessageType message);

    String senderName();
}
