package nl.avans.wordcrex.model;

public enum Role {
    PLAYER("player"),
    OBSERVER("observer"),
    MODERATOR("moderator"),
    ADMINISTRATOR("administrator");

    public final String role;

    Role(String role) {
        this.role = role;
    }

    public static Role byRole(String role) {
        for (var r : Role.values()) {
            if (r.role.equals(role)) {
                return r;
            }
        }

        return null;
    }
}
