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
    public final int initialX;
    public final int initialY;
    public final int width;
    public final int height;
    public final boolean enabled;

    private final BiConsumer<Graphics2D, Boolean> draw;
    private final BiFunction<Integer, Integer, Pair<Integer, Integer>> absolute;
    private final BiFunction<Integer, Integer, Pair<Integer, Integer>> relative;
    private final BiConsumer<Pair<Integer, Integer>, Boolean> state;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private boolean hover;
    private boolean dragging;

    public DragWidget(int x, int y, int width, int height, boolean enabled, BiConsumer<Graphics2D, Boolean> draw, BiFunction<Integer, Integer, Pair<Integer, Integer>> absolute, BiFunction<Integer, Integer, Pair<Integer, Integer>> relative, BiConsumer<Pair<Integer, Integer>, Boolean> state) {
        this.x = this.initialX = x;
        this.y = this.initialY = y;
        this.width = width;
        this.height = height;
        this.enabled = enabled;
        this.draw = draw;
        this.absolute = absolute;
        this.relative = relative;
        this.state = state;
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.dragging) {
            var r = this.relative.apply(this.x + this.offsetX, this.y + this.offsetY);

            if (r != null) {
                var a = this.absolute.apply(r.a, r.b);

                g.setColor(Colors.OVERLAY);
                g.fillRect(a.a, a.b, this.width, this.height);
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
        this.hover = this.enabled && x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;
    }

    @Override
    public void mousePress(int x, int y) {
        this.dragging = this.hover;

        if (this.dragging) {
            this.offsetX = x - this.x;
            this.offsetY = y - this.y;

            var r = this.relative.apply(this.x, this.y);

            if (r != null) {
                this.state.accept(r, false);
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
            var r = this.relative.apply(x, y);

            if (r == null) {
                this.x = this.initialX;
                this.y = this.initialY;
            } else {
                var a = this.absolute.apply(r.a, r.b);

                this.x = a.a;
                this.y = a.b;
                this.state.accept(r, true);
            }
        }

        this.dragging = false;
    }
}
