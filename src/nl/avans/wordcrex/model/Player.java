package nl.avans.wordcrex.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final String name;
    public final String firstName;
    public final String lastName;

    private final List<Role> roles;

    public Player(String name, String firstName, String lastName) {
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = new ArrayList<>();
    }

    public String getDisplayName() {
        if (this.firstName != null && this.lastName != null) {
            return this.firstName + " " + this.lastName;
        }

        return this.name;
    }
}
