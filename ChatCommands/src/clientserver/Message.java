package clientserver;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class Message implements Serializable, Comparable<Message> {
    private final String sender;
    private final Long timestamp;
    private final String date;
    private final String text;

    public Message(String sender, String text) {
        this.sender = sender;
        Date date = new Date();
        this.timestamp = date.getTime();
        this.date = DateFormat.getInstance().format(date);
        this.text = text;
    }

    public Message(String sender, Long timestamp, String date, String text) {
        this.sender = sender;
        this.timestamp = timestamp;
        this.date = date;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int compareTo(Message message) {
        return (int) (this.timestamp - message.timestamp);
    }
}
