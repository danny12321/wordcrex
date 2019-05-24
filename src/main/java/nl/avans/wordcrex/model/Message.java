package nl.avans.wordcrex.model;

import java.util.Date;

public class Message {
    public final User user;
    public final Date date;
    public final String message;

    public Message(User user, Date date, String message) {
        this.user = user;
        this.date = date;
        this.message = message;
    }
}
