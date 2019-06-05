package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.function.Consumer;

public class ScrollbarWidget extends Widget {
    private final Consumer<Integer> scroll;

    private int height;
    private int offset = -1;
    private boolean hover;
    private boolean dragging;
    private int from;
    private boolean reverse;
    private boolean lock;

    public ScrollbarWidget(Consumer<Integer> scroll) {
        this.scroll = scroll;
    }

    public ScrollbarWidget(Consumer<Integer> scroll, boolean reverse) {
        this.scroll = scroll;
        this.reverse = reverse;
        if(reverse) {
            this.lock = true;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        var height = Main.FRAME_SIZE - Main.TASKBAR_SIZE;

        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(Main.FRAME_SIZE - Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, height);

        var extra = this.height - Main.FRAME_SIZE + Main.TASKBAR_SIZE;
        var scroller = (float) height / this.height * (float) height;

        if (extra > 0) {
            g.setColor(this.hover ? Colors.DARKER_YELLOW : Colors.DARK_YELLOW);
            g.fillRect(Main.FRAME_SIZE - Main.TASKBAR_SIZE, Main.TASKBAR_SIZE + this.offset, Main.TASKBAR_SIZE, (int) scroller);
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public void mouseMove(int x, int y) {
        var extra = this.height - Main.FRAME_SIZE + Main.TASKBAR_SIZE;

        if (extra <= 0) {
            return;
        }

        var height = Main.FRAME_SIZE - Main.TASKBAR_SIZE;
        var scroller = (float) height / this.height * (float) height;
        var position = Main.TASKBAR_SIZE + this.offset;

        this.hover = x > Main.FRAME_SIZE - Main.TASKBAR_SIZE && y > position && y < position + scroller;
    }

    @Override
    public void mousePress(int x, int y) {
        if (!this.hover) {
            return;
        }

        this.dragging = true;
        this.from = y - this.offset;
    }

    @Override
    public void mouseDrag(int x, int y) {
        if (!this.dragging) {
            return;
        }

        var extra = this.height - Main.FRAME_SIZE + Main.TASKBAR_SIZE;
        var height = Main.FRAME_SIZE - Main.TASKBAR_SIZE;
        var scroller = (float) height / this.height * (float) height;

        this.offset = (int) Math.min(height - scroller, Math.max(0, y - this.from));

        var scroll = 0;

        if (this.reverse) {
            scroll = (this.height - height) - (int) (this.offset / (height - scroller) * extra);
        } else {
            scroll = (int) (this.offset / (height - scroller) * extra);
        }

        this.lock = !this.reverse || !(height - (this.offset + scroller) > 5);

        this.scroll.accept(scroll);
    }

    @Override
    public void mouseRelease(int x, int y) {
        this.dragging = false;
    }

    public void setHeight(int height) {
        this.height = height;


        if (this.reverse && this.lock) {
            var e = Main.FRAME_SIZE - Main.TASKBAR_SIZE;
            var scroller = (int) ((float) e / height * (float) e);

            this.offset = e - scroller;
        }
    }
}
