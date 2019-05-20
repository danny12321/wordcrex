package nl.avans.wordcrex.model;

public enum GameState {
    REQUESTED("request"),
    PLAYING("playing"),
    FINISHED("finished"),
    RESIGNED("resigned");

    public final String state;

    GameState(String state) {
        this.state = state;
    }

    public static GameState byState(String state) {
        for (var s : GameState.values()) {
            if (s.state.equals(state)) {
                return s;
            }
        }

        return null;
    }
}
