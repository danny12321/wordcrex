package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DragWidget extends Widget {
    public final int width;
    public final int height;
    public final int initialX;
    public final int initialY;

    private final BiConsumer<Graphics2D, Boolean> draw;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private boolean hover;
    private boolean dragging;

    public DragWidget(int x, int y, int width, int height, BiConsumer<Graphics2D, Boolean> draw) {
        this.x = this.initialX = x;
        this.y = this.initialY = y;
        this.width = width;
        this.height = height;
        this.draw = draw;
    }

    @Override
    public void draw(Graphics2D g) {
        g.translate(this.x, this.y);
        this.draw.accept(g, this.hover);
        g.translate(-this.x, -this.y);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public void mouseMove(int x, int y) {
        this.hover = x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;
    }

    @Override
    public void mousePress(int x, int y) {
        this.dragging = this.hover;

        if (this.dragging) {
            this.offsetX = x - this.x;
            this.offsetY = y - this.y;
        }
    }

    @Override
    public void mouseDrag(int x, int y) {
        if (this.dragging) {
            this.x = x - this.offsetX;
            this.y = y - this.offsetY;
        }
    }

    @Override
    public void mouseRelease(int x, int y) {
        if (this.dragging) {
            this.x = this.initialX;
            this.y = this.initialY;
        }

        this.dragging = false;
    }
}
