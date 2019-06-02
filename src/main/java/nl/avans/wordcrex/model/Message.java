package nl.avans.wordcrex.model;

import java.util.Date;

public class Message {
    public final String message;
    public final String user;
    public final Date date;

    public Message(String message, String user, Date date) {
        this.message = message;
        this.user = user;
        this.date = date;
    }
}
