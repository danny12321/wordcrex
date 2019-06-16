package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.TileAxis;
import nl.avans.wordcrex.model.TileSide;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DragWidget<T> extends Widget {
    public final T data;
    public final int initialX;
    public final int initialY;
    public final int width;
    public final int height;

    private final BiConsumer<Graphics2D, Pair<Boolean, Boolean>> draw;
    private final BiFunction<Integer, Integer, Pair<Integer, Integer>> absolute;
    private final BiFunction<Integer, Integer, Pair<Integer, Integer>> relative;
    private final BiFunction<Integer, Integer, Boolean> check;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private boolean hover;
    private boolean dragging;
    private boolean enabled;

    public DragWidget(T data, int x, int y, int width, int height, boolean enabled, BiConsumer<Graphics2D, Pair<Boolean, Boolean>> draw, BiFunction<Integer, Integer, Pair<Integer, Integer>> absolute, BiFunction<Integer, Integer, Pair<Integer, Integer>> relative, BiFunction<Integer, Integer, Boolean> check) {
        this.data = data;
        this.x = this.initialX = x;
        this.y = this.initialY = y;
        this.width = width;
        this.height = height;
        this.enabled = enabled;
        this.draw = draw;
        this.absolute = absolute;
        this.relative = relative;
        this.check = check;
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.dragging) {
            var r = this.relative.apply(this.x + this.offsetX, this.y + this.offsetY);

            if (r != null && this.check.apply(r.a, r.b)) {
                var a = this.absolute.apply(r.a, r.b);

                g.setColor(Colors.OVERLAY);
                g.fillRect(a.a, a.b, this.width, this.height);
            }
        }

        g.translate(this.x, this.y);
        this.draw.accept(g, new Pair<>(this.hover, this.hasFocus()));
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

            this.requestFocus();
        } else {
            this.setFocus(false);
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

            if (r == null || !this.check.apply(r.a, r.b)) {
                this.x = this.initialX;
                this.y = this.initialY;
            } else {
                var a = this.absolute.apply(r.a, r.b);

                this.x = a.a;
                this.y = a.b;
            }
        }

        this.dragging = false;
        this.mouseMove(x, y);
    }

    @Override
    public void keyPress(int code, int modifiers) {
        if (!this.hasFocus()) {
            return;
        }

        TileSide side = null;

        if (code == KeyEvent.VK_UP) {
            side = TileSide.SOUTH;
        } else if (code == KeyEvent.VK_RIGHT) {
            side = TileSide.WEST;
        } else if (code == KeyEvent.VK_DOWN) {
            side = TileSide.NORTH;
        } else if (code == KeyEvent.VK_LEFT) {
            side = TileSide.EAST;
        }

        if (side == null) {
            return;
        }

        var width = this.getSize((i) -> new Pair<>(i, 1));
        var height = this.getSize((i) -> new Pair<>(1, i));
        var free = new ArrayList<Pair<Integer, Integer>>();

        for (var i = 0; i < width * height; i++) {
            var x = i % width + 1;
            var y = i / width + 1;

            if (!this.check.apply(x, y)) {
                continue;
            }

            free.add(this.absolute.apply(x, y));
        }

        var s = side;
        var closest = free.stream()
            .filter((p) -> this.isSide(p, s))
            .min(Comparator.comparingDouble((p) -> {
                var x = p.a - this.x;
                var y = p.b - this.y;

                return Math.sqrt(x * x + y * y);
            }))
            .orElse(null);

        if (closest == null) {
            this.x = this.initialX;
            this.y = this.initialY;

            return;
        }

        this.x = closest.a;
        this.y = closest.b;
    }

    @Override
    public boolean top() {
        return this.dragging;
    }

    @Override
    public boolean focusable() {
        return this.enabled;
    }

    public void setPosition(int x, int y) {
        if (this.dragging) {
            return;
        }

        var pos = this.absolute.apply(x, y);

        if (pos == null) {
            this.x = this.initialX;
            this.y = this.initialY;

            return;
        }

        this.x = pos.a;
        this.y = pos.b;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Pair<Integer, Integer> getPosition() {
        if (this.dragging) {
            return null;
        }

        return this.relative.apply(this.x, this.y);
    }

    private boolean isSide(Pair<Integer, Integer> pair, TileSide side) {
        if (side.axis == TileAxis.HORIZONTAL) {
            return ((int) Math.signum(this.x - pair.a)) == side.x;
        } else {
            return ((int) Math.signum(this.y - pair.b)) == side.y;
        }
    }

    private int getSize(Function<Integer, Pair<Integer, Integer>> coords) {
        var size = 0;
        Pair<Integer, Integer> pair;

        do {
            size++;
            pair = coords.apply(size);
        } while (this.absolute.apply(pair.a, pair.b) != null);

        return size - 1;
    }
}
