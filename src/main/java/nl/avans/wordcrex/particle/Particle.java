package nl.avans.wordcrex.particle;

import nl.avans.wordcrex.Main;

import java.awt.*;
import java.util.function.Consumer;

public abstract class Particle {
    public final float gravity;
    public final boolean foreground;
    public final boolean persistent;

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;

    public Particle(int x, int y, float velocityX, float velocityY, float gravity, boolean foreground, boolean persistent) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.gravity = gravity;
        this.foreground = foreground;
        this.persistent = persistent;
    }

    public abstract void draw(Graphics2D g);

    public boolean update(Consumer<Particle> addParticle) {
        this.x += this.velocityX;
        this.y += this.velocityY;
        this.velocityX *= this.gravity;
        this.velocityY *= this.gravity;

        return this.getX() >= 0 && this.getX() <= Main.FRAME_SIZE && this.getY() >= 0 && this.getY() <= Main.FRAME_SIZE;
    }

    public int getX() {
        return (int) this.x;
    }

    public int getY() {
        return (int) this.y;
    }

    public int priority() {
        return 0;
    }
}
