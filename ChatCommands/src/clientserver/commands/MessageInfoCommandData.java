package clientserver.commands;

import clientserver.Message;

import java.io.Serializable;

public class MessageInfoCommandData implements Serializable {

    private final Message message;
    private final String sender;

    public MessageInfoCommandData(Message message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public Message getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
