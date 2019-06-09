package nl.avans.wordcrex.model;

public enum UserPoll {
    GAMES(UserRole.PLAYER),
    OBSERVABLE(UserRole.OBSERVER),
    WORDS(UserRole.PLAYER),
    APPROVABLE(UserRole.MODERATOR),
    MANAGEABLE(UserRole.ADMINISTRATOR);

    public final UserRole role;

    UserPoll(UserRole role) {
        this.role = role;
    }
}
