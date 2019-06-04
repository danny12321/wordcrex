package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

public class ButtonWidget extends Widget {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Color background;
    private final Color backgroundHover;
    private final Color foreground;
    private final Runnable runnable;

    private String text;
    private boolean hover;
    private boolean enabled = true;
    private boolean visible = true;

    public ButtonWidget(String text, int x, int y, int width, int height, Runnable runnable) {
        this(text, x, y, width, height, Colors.DARK_YELLOW, Colors.DARKER_YELLOW, Colors.DARKER_BLUE, runnable);
    }

    public ButtonWidget(String text, int x, int y, int width, int height, Color background, Color hover, Color foreground, Runnable runnable) {
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
    public void draw(Graphics2D g) {
        if (!this.visible) {
            return;
        }

        if (this.hasFocus()) {
            g.setColor(Color.white);
            g.fillRect(this.x - 2, this.y - 2, this.width + 4, this.height + 4);
        }

        g.setColor(this.hover || !this.enabled ? this.backgroundHover : this.background);
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(this.foreground);
        StringUtil.drawCenteredString(g, this.x, this.y, this.width, this.height, this.text);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public void mouseMove(int x, int y) {
        this.hover = x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.hover && this.enabled && this.visible) {
            this.runnable.run();
        }

        if (!this.hover) {
            this.setFocus(false);
        }
    }

    @Override
    public void keyPress(int code, int modifiers) {
        if (code == KeyEvent.VK_ENTER && this.hasFocus() && this.enabled && this.visible) {
            this.runnable.run();
        }
    }

    @Override
    public List<Widget> children() {
        return List.of();
    }

    @Override
    public boolean canFocus() {
        return this.enabled && this.visible;
    }

    public void setText(String text) {
        this.text = text;
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
