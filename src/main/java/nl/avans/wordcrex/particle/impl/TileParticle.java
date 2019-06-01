package nl.avans.wordcrex.particle.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;

import java.awt.*;
import java.util.function.Consumer;

public class TileParticle extends Particle {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final char character;

    private int update;

    public TileParticle(int x, float velocityX, float velocityY) {
        super(x, 0, velocityX, velocityY, 1.0f, false, false);
        this.character = TileParticle.ALPHABET.charAt(Main.RANDOM.nextInt(TileParticle.ALPHABET.length()));
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Colors.DARK_BLUE);
        g.fillRect(this.getX(), this.getY(), 24, 24);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, this.getX(), this.getY(), 24, 24, String.valueOf(this.character));
    }

    @Override
    public boolean update(Consumer<Particle> addParticle) {
        if (this.update++ % 2 == 0) {
            addParticle.accept(new TrailParticle(this.getX() + 12, this.getY() + 12));
        }

        return super.update(addParticle);
    }
}
