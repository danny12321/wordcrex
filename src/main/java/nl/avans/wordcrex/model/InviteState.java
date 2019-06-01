package nl.avans.wordcrex.model;

public enum InviteState {
    PENDING("unknown"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    public final String state;

    InviteState(String state) {
        this.state = state;
    }

    public static InviteState byState(String state) {
        for (var s : InviteState.values()) {
            if (s.state.equals(state)) {
                return s;
            }
        }

        throw new RuntimeException("Invalid invite state: " + state);
    }
}
