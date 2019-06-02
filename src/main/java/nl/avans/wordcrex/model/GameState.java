package nl.avans.wordcrex.model;

public enum GameState {
    PENDING("request", 0),
    PLAYING("playing", 1),
    FINISHED("finished", 2),
    RESIGNED("resigned", 3);

    public final String state;
    public final int order;

    GameState(String state, int order) {
        this.state = state;
        this.order = order;
    }

    public static GameState byState(String state) {
        for (var s : GameState.values()) {
            if (s.state.equals(state)) {
                return s;
            }
        }

        throw new RuntimeException("Invalid game state: " + state);
    }
}
