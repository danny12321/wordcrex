package nl.avans.wordcrex.model;

public enum Role {
    PLAYER("player"),
    OBSERVER("observer"),
    MODERATOR("moderator"),
    ADMINISTRATOR("administrator");

    public final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role byName(String name) {
        for (var role : Role.values()) {
            if (role.name.equals(name)) {
                return role;
            }
        }

        return null;
    }
}
