package nl.avans.wordcrex.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getInitial() {
        return this.getDisplayName().substring(0, 1).toUpperCase();
    }

    public Map<String, String> getStatistics() {
        var statistics = new HashMap<String, String>();

        statistics.put("Games won", "20");
        statistics.put("Games lost", "5");
        statistics.put("Games tied", "2");
        statistics.put("Games forfeited", "1");
        statistics.put("Highest game score", "420");
        statistics.put("Highest word score", "woordje (80)");
        statistics.put("Highest game bonus", "0");
        statistics.put("Total seven letter bonus", "0");

        return statistics;
    }
}
