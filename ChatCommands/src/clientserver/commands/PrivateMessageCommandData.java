package clientserver.commands;

import clientserver.Message;

import java.io.Serializable;

public class PrivateMessageCommandData implements Serializable {

    private final String receiver;
    private final Message message;

    public PrivateMessageCommandData(String receiver, Message message) {
        this.receiver = receiver;
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public Message getMessage() {
        return message;
    }
}
