package nl.avans.wordcrex.model;

public enum UserPoll {
    GAMES(UserRole.PLAYER),
    OBSERVABLE(UserRole.OBSERVER),
    WORDS(UserRole.PLAYER),
    MANAGEABLE(UserRole.MODERATOR);

    public final UserRole role;

    UserPoll(UserRole role) {
        this.role = role;
    }
}
