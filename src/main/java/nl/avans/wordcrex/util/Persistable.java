package nl.avans.wordcrex.util;

import nl.avans.wordcrex.model.Wordcrex;

public interface Persistable {
    Wordcrex persist(Wordcrex model);
}
