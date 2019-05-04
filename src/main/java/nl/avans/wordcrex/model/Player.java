package nl.avans.wordcrex.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final int id;
    public final String username;
    public final String firstName;
    public final String lastName;

    private final List<Role> roles = new ArrayList<>();

    public Player(int id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public String getDisplayName() {
        if (this.firstName != null && this.lastName != null) {
            return this.firstName + " " + this.lastName;
        }

        return this.username;
    }
}
