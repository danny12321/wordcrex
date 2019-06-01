package nl.avans.wordcrex.model;

public enum TurnAction {
    PASS("pass"),
    PLAY("play"),
    RESIGN("resign");

    public final String action;

    TurnAction(String action) {
        this.action = action;
    }

    public static TurnAction byAction(String action) {
        for (var s : TurnAction.values()) {
            if (s.action.equals(action)) {
                return s;
            }
        }

        return null;
    }
}
