package nl.avans.wordcrex.model;

public enum WordState {
    ACCEPTED("accepted"),
    REJECTED("denied"),
    PENDING("pending");

    public final String state;

    WordState(String state) {
        this.state = state;
    }

    public static WordState byState(String state) {
        for (var s : WordState.values()) {
            if (s.state.equals(state)) {
                return s;
            }
        }

        return null;
    }
}
