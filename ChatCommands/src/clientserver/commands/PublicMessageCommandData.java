package clientserver.commands;

import clientserver.Message;

import java.io.Serializable;

public class PublicMessageCommandData implements Serializable {

    private final String sender;
    private final Message message;

    public PublicMessageCommandData(String sender, Message message) {
        this.sender = sender;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
