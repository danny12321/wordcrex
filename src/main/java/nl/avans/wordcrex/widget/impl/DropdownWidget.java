package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DropdownWidget<T> extends Widget {
    private final Map<T, String> options;
    private final String placeholder;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Consumer<T> consumer;

    private int hover = -1;
    private T selected;
    private boolean open;

    public DropdownWidget(Map<T, String> options, String placeholder, int x, int y, int width, int height, Consumer<T> consumer) {
        this.options = options;
        this.placeholder = placeholder;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.consumer = consumer;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.hover == 0 && !this.hasFocus() ? Colors.DARKER_YELLOW : Colors.DARK_YELLOW);
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(Colors.DARKER_BLUE);
        g.drawString(this.selected != null ? this.options.get(this.selected) : this.placeholder, this.x + 16, this.y + this.height / 2 + 5);

        if (this.open) {
            var index = new AtomicInteger(1);

            this.options.forEach((key, value) -> {
                var offset = this.y + index.get() * this.height;

                g.setColor(this.hover == index.get() ? Color.LIGHT_GRAY : Color.WHITE);
                g.fillRect(this.x, offset, this.width, this.height);
                g.setColor(Colors.DARKER_BLUE);
                g.drawString(value, this.x + 16, offset + this.height / 2 + 5);

                index.getAndIncrement();
            });
        }

        if (this.hasFocus()) {
            g.setColor(Color.white);
            g.drawRect(this.x, this.y, this.width - 2, this.height - 2);
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public void mouseMove(int x, int y) {
        this.hover = -1;

        if (x <= this.x || x >= this.x + this.width) {
            return;
        }

        if (y > this.y && y < this.y + this.height) {
            this.hover = 0;
        } else if (this.open && y > this.y + this.height) {
            this.hover = (y - this.y) / this.height;
        }
    }

    @Override
    public void mousePress(int x, int y) {
        this.open = this.hover == 0 && !this.open;
        this.select();

        if (this.open) {
            this.requestFocus();
        } else if (this.hover < 0) {
            this.setFocus(false);
        }
    }

    @Override
    public void keyPress(int code, int modifiers) {
        if (!this.hasFocus()) {
            return;
        }

        if (code == KeyEvent.VK_ENTER) {
            if (this.open) {
                this.select();
            }

            this.open = !this.open;
            this.hover = 0;
        } else if (code == KeyEvent.VK_DOWN) {
            this.hover = Math.min(this.open ? this.options.size() : 0, this.hover + 1);
        } else if (code == KeyEvent.VK_UP) {
            this.hover = Math.max(0, this.hover - 1);
        }
    }

    @Override
    public boolean blocking() {
        return this.open;
    }

    @Override
    public boolean focusable() {
        return !this.options.isEmpty();
    }

    @Override
    public void setFocus(boolean focus) {
        this.hover = focus ? 0 : -1;

        super.setFocus(focus);
    }

    private void select() {
        if (this.hover < 1 || this.hover > this.options.size()) {
            return;
        }

        var keys = List.copyOf(this.options.keySet());

        this.consumer.accept(keys.get(this.hover - 1));
        this.selected = keys.get(this.hover - 1);
    }
}
