package nl.avans.wordcrex.model;

public enum TurnAction {
    PLAY("play"),
    PASS("pass"),
    RESIGN("resign");

    public final String action;

    TurnAction(String action) {
        this.action = action;
    }
}
