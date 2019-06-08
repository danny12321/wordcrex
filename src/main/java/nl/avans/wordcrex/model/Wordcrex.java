package nl.avans.wordcrex.model2;

import nl.avans.wordcrex.util.Persistable;

public class Wordcrex implements Persistable {
    public final User user;

    public Wordcrex(User user) {
        this.user = user;
    }

    @Override
    public Wordcrex persist() {
        return this;
    }

    public Wordcrex register(String username, String password) {
        return this.login(username, password);
    }

    public Wordcrex login(String username, String password) {
        return this;
    }

    public Wordcrex logout() {
        return this;
    }
}
