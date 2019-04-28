package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.util.function.Consumer;

public class ScrollUI extends UI {
    private final Consumer<Integer> scroll;

    private int height;

    private int offset;
    private boolean hover;
    private boolean dragging;
    private int from;

    public ScrollUI(int height, Consumer<Integer> scroll) {
        this.height = height;
        this.scroll = scroll;
    }

    @Override
    public void initialize(GamePanel game, SwingController controller) {
    }

    @Override
    public void draw(Graphics2D g) {
        var height = SwingView.SIZE - GamePanel.TASKBAR_SIZE;

        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(SwingView.SIZE - GamePanel.TASKBAR_SIZE, GamePanel.TASKBAR_SIZE, GamePanel.TASKBAR_SIZE, height);

        var extra = this.height - SwingView.SIZE + GamePanel.TASKBAR_SIZE;
        var scroller = (float) height / this.height * (float) height;

        if (extra > 0) {
            g.setColor(this.hover ? Colors.DARKER_YELLOW : Colors.DARK_YELLOW);
            g.fillRect(SwingView.SIZE - GamePanel.TASKBAR_SIZE, GamePanel.TASKBAR_SIZE + this.offset, GamePanel.TASKBAR_SIZE, (int) scroller);
        }
    }

    @Override
    public int mouseMove(int x, int y) {
        var extra = this.height - SwingView.SIZE + GamePanel.TASKBAR_SIZE;

        if (extra <= 0) {
            return Cursor.DEFAULT_CURSOR;
        }

        var height = SwingView.SIZE - GamePanel.TASKBAR_SIZE;
        var scroller = (float) height / this.height * (float) height;
        var position = GamePanel.TASKBAR_SIZE + this.offset;

        this.hover = x > SwingView.SIZE - GamePanel.TASKBAR_SIZE && y > position && y < position + scroller;

        return this.hover ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
    }

    @Override
    public void mousePress(int x, int y) {
        if (this.hover) {
            this.dragging = true;
            this.from = y - this.offset;
        }
    }

    @Override
    public void mouseDrag(int x, int y) {
        if (this.dragging) {
            var extra = this.height - SwingView.SIZE + GamePanel.TASKBAR_SIZE;
            var height = SwingView.SIZE - GamePanel.TASKBAR_SIZE;
            var scroller = (float) height / this.height * (float) height;

            this.offset = (int) Math.min(height - scroller, Math.max(0, y - this.from));
            this.scroll.accept((int) (this.offset / (height - scroller) * extra));
        }
    }

    @Override
    public void mouseRelease() {
        this.dragging = false;
    }
}
