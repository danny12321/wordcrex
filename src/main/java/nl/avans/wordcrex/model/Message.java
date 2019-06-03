package nl.avans.wordcrex.model;

import java.util.Date;

public class Message {
    public final String message;
    public final String username;
    public final Date date;

    public Message(String message, String username, Date date) {
        this.message = message;
        this.username = username;
        this.date = date;
    }
}
