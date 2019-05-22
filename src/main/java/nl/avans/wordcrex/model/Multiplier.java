package nl.avans.wordcrex.model;

public class Multiplier {
    public final MultiplierType type;
    public final int multiplier;

    public Multiplier(String multiplier) {
        this.type = MultiplierType.byType(multiplier.charAt(1));
        this.multiplier = Integer.parseInt(String.valueOf(multiplier.charAt(0)));
    }
}
