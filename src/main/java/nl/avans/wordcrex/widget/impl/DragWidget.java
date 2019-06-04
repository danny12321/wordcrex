package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class DragWidget extends Widget {
    public final int width;
    public final int height;
    public final int initialX;
    public final int initialY;

    private final BiConsumer<Graphics2D, Boolean> draw;
    private final BiFunction<Integer, Integer, Pair<Integer, Integer>> check;
    private final BiConsumer<Pair<Integer, Integer>, Boolean> state;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private boolean hover;
    private boolean dragging;

    public DragWidget(int x, int y, int width, int height, BiConsumer<Graphics2D, Boolean> draw, BiFunction<Integer, Integer, Pair<Integer, Integer>> check, BiConsumer<Pair<Integer, Integer>, Boolean> state) {
        this.x = this.initialX = x;
        this.y = this.initialY = y;
        this.width = width;
        this.height = height;
        this.draw = draw;
        this.check = check;
        this.state = state;
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.dragging) {
            var target = this.check.apply(this.x + this.offsetX, this.y + this.offsetY);

            if (target != null) {
                g.setColor(Colors.OVERLAY);
                g.fillRect(target.a, target.b, this.width, this.height);
            }
        }

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

            if (this.x != this.initialX && this.y != this.initialY) {
                this.state.accept(new Pair<>(this.x, this.y), false);
            }
        }
    }

    @Override
    public void mouseDrag(int x, int y) {
        if (this.dragging) {
            this.x = Math.max(0, Math.min(Main.FRAME_SIZE - this.width, x - this.offsetX));
            this.y = Math.max(Main.TASKBAR_SIZE, Math.min(Main.FRAME_SIZE - this.height, y - this.offsetY));
        }
    }

    @Override
    public void mouseRelease(int x, int y) {
        if (this.dragging) {
            var target = this.check.apply(x, y);

            if (target == null) {
                this.x = this.initialX;
                this.y = this.initialY;
            } else {
                this.x = target.a;
                this.y = target.b;
                this.state.accept(new Pair<>(this.x, this.y), true);
            }
        }

        this.dragging = false;
    }
}
