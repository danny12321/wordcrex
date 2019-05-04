package nl.avans.wordcrex.model;

import java.util.List;

public class Player {
    public final int id;
    public final String username;
    public final String firstName;
    public final String lastName;
    public final List<Role> roles;

    public Player(int id, String username, String firstName, String lastName, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public String getDisplayName() {
        if (this.firstName != null && this.lastName != null) {
            return this.firstName + " " + this.lastName;
        }

        return this.username;
    }
}
