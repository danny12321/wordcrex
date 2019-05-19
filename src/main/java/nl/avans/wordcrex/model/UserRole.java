package nl.avans.wordcrex.model;

public enum UserRole {
    PLAYER("player"),
    OBSERVER("observer"),
    MODERATOR("moderator"),
    ADMINISTRATOR("administrator");

    public final String role;

    UserRole(String role) {
        this.role = role;
    }

    public static UserRole byRole(String role) {
        for (var r : UserRole.values()) {
            if (r.role.equals(role)) {
                return r;
            }
        }

        return null;
    }
}
