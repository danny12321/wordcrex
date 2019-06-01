package nl.avans.wordcrex.model;

import java.util.Date;

public class Message implements Comparable<Message> {
    public final User user;
    public final Date date;
    public final String message;

    public Message(User user, Date date, String message) {
        this.user = user;
        this.date = date;
        this.message = message;
    }

    @Override
    public int compareTo(Message m) {
        if(this.date.equals(m.date)) return 0;
        return this.date.after(m.date) ? 1 : -1;
    }
}
