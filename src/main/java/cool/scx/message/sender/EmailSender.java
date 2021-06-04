package cool.scx.message.sender;

import cool.scx.message.BaseSender;

public class EmailSender implements BaseSender<String, String> {


    public EmailSender() {

    }

    @Override
    public void send(String address, String message) {

    }

    @Override
    public String senderName() {
        return "email";
    }
}
