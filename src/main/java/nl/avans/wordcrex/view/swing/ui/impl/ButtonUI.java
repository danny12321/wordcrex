package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;

public class ButtonUI extends UI {
    private String text;
    private int x;
    private int y;
    private final int width;
    private final int height;
    private final Color background;
    private final Color backgroundHover;
    private final Color foreground;
    private final Runnable runnable;

    private boolean hover;
    private boolean enabled = true;
    private boolean visible = true;

    public ButtonUI(String text, int x, int y, int width, int height, Runnable runnable) {
        this(text, x, y, width, height, Colors.DARK_YELLOW, Colors.DARKER_YELLOW, Colors.DARKER_BLUE, runnable);
    }

    public ButtonUI(String text, int x, int y, int width, int height, Color background, Color hover, Color foreground, Runnable runnable) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.background = background;
        this.backgroundHover = hover;
        this.foreground = foreground;
        this.runnable = runnable;
    }

    @Override
    public void initialize(GamePanel game, SwingController controller) {
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.visible) {
            return;
        }

        g.setColor(this.hover || !this.enabled ? this.backgroundHover : this.background);
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(this.foreground);
        StringUtil.drawCenteredString(g, this.x, this.y, this.width, this.height, this.text);
    }

    @Override
    public int mouseMove(int x, int y) {
        if (!this.visible || !this.enabled) {
            return Cursor.DEFAULT_CURSOR;
        }

        this.hover = x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;

        return this.hover ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.hover) {
            this.runnable.run();
        }
    }

    @Override
    public int mouseDrag(int x, int y) {
        return this.hover ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
    }

    @Override
    public int mouseRelease(int x, int y) {
        return this.hover ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!this.enabled) {
            this.hover = false;
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;

        if (!this.enabled) {
            this.hover = false;
        }
    }
}
