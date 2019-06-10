package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class ButtonWidget extends Widget {
    private final int width;
    private final int height;
    private final String tooltip;
    private final Color background;
    private final Color backgroundHover;
    private final Color foreground;
    private final Runnable runnable;

    private BufferedImage image;
    private String text;
    private int x;
    private int y;
    private boolean hover;
    private boolean enabled = true;
    private boolean visible = true;

    public ButtonWidget(String text, int x, int y, int width, int height, Runnable runnable) {
        this(text, null, null, x, y, width, height, Colors.DARK_YELLOW, Colors.DARKER_YELLOW, Colors.DARKER_BLUE, runnable);
    }

    public ButtonWidget(BufferedImage image, String tooltip, int x, int y, int width, int height, Runnable runnable) {
        this(null, image, tooltip, x, y, width, height, Colors.DARK_YELLOW, Colors.DARKER_YELLOW, Colors.DARKER_BLUE, runnable);
    }

    public ButtonWidget(String text, BufferedImage image, String tooltip, int x, int y, int width, int height, Color background, Color hover, Color foreground, Runnable runnable) {
        this.text = text;
        this.image = image;
        this.tooltip = tooltip;
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

        g.setColor(this.hover || !this.enabled ? this.backgroundHover : this.background);
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(this.foreground);

        if (this.image != null) {
            g.drawImage(this.image, this.x, this.y, this.width, this.height, null);
        } else {
            StringUtil.drawCenteredString(g, this.x, this.y, this.width, this.height, this.text);
        }

        if (this.hasFocus()) {
            g.setColor(Color.white);
            g.drawRect(this.x, this.y, this.width - 2, this.height - 2);
        }

        if (!this.hover || this.tooltip == null) {
            return;
        }

        var metrics = g.getFontMetrics();
        var width = metrics.stringWidth(this.tooltip) + 16;
        var x = this.x + this.width + 12;

        g.setColor(Color.BLACK);
        g.fillRect(x, this.y, width, this.height);
        g.fillPolygon(new int[]{x - 7, x, x}, new int[]{this.y + 16, this.y + 9, this.y + 23}, 3);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, x, this.y, width, this.height, this.tooltip);
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
        if (this.hover && this.enabled && this.visible) {
            this.requestFocus();
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
    public boolean focusable() {
        return this.enabled && this.visible;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
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
