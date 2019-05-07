package nl.avans.wordcrex.util;

import nl.avans.wordcrex.model.User;

public interface Pollable<T> {
    T poll();

    User persist(User user);
}
