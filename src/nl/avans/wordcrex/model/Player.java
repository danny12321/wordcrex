package nl.avans.wordcrex.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final String name;
    public final String fullName;

    private final List<Role> roles;

    public Player(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
        this.roles = new ArrayList<>();
    }

    public String getDisplayName() {
        if (this.fullName != null) {
            return this.fullName;
        }

        return this.name;
    }
}
