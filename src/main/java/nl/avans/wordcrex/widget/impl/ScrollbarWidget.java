package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class ScrollbarWidget extends Widget {
    private final Consumer<Integer> scroll;
    private final boolean reverse;

    private int height;
    private int offset;
    private boolean hover;
    private boolean dragging;
    private int from;
    private boolean lock;

    public ScrollbarWidget(Consumer<Integer> scroll) {
        this(scroll, false);
    }

    public ScrollbarWidget(Consumer<Integer> scroll, boolean reverse) {
        this.scroll = scroll;
        this.reverse = reverse;
        this.lock = reverse;

        if (this.lock) {
            this.setOffset(-1);
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

        if (this.hasFocus()) {
            g.setColor(Color.white);
            g.drawRect(Main.FRAME_SIZE - Main.TASKBAR_SIZE, Main.TASKBAR_SIZE + this.offset, Main.TASKBAR_SIZE - 1, (int) scroller - 1);
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
            this.setFocus(false);

            return;
        }

        this.requestFocus();
        this.dragging = true;
        this.from = y - this.offset;
    }

    @Override
    public void mouseDrag(int x, int y) {
        if (!this.dragging) {
            return;
        }

        var height = Main.FRAME_SIZE - Main.TASKBAR_SIZE;

        this.setOffset((int) ((y - this.from) / (float) height * (float) this.height));
    }

    @Override
    public void mouseRelease(int x, int y) {
        this.dragging = false;
    }

    @Override
    public void keyPress(int code, int modifiers) {
        if (!this.hasFocus()) {
            return;
        }

        var height = Main.FRAME_SIZE - Main.TASKBAR_SIZE;
        var next = (int) ((float) this.offset / (float) height * (float) this.height);

        if (code == KeyEvent.VK_UP) {
            next -= 20;
        } else if (code == KeyEvent.VK_DOWN) {
            next += 20;
        }

        this.setOffset(next);
    }

    @Override
    public boolean focusable() {
        return this.height - Main.FRAME_SIZE + Main.TASKBAR_SIZE > 0;
    }

    public void setHeight(int height) {
        var last = this.height;

        this.height = height;

        if (this.lock && last != height) {
            this.setOffset(-1);
        }
    }

    public void setOffset(int offset) {
        var extra = this.height - Main.FRAME_SIZE + Main.TASKBAR_SIZE;
        var height = Main.FRAME_SIZE - Main.TASKBAR_SIZE;
        var scroller = (float) height / this.height * (float) height;
        var bottom = (int) (height - scroller);
        var y = offset == -1 ? bottom : (float) offset / (float) this.height * (float) height;

        this.offset = (int) Math.min(bottom, Math.max(0, y));
        this.lock = this.offset == bottom && this.reverse;
        this.scroll.accept((int) (this.offset / (float) bottom * extra));
    }
}
