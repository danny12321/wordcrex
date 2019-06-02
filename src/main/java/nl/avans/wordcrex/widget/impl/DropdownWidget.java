package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DropdownWidget<T> extends Widget {
    private final Map<T, String> options;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Consumer<T> consumer;
    private final String placeholder;

    private int hover = -1;
    private T selected;
    private boolean open;

    public DropdownWidget(Map<T, String> options, String placeholder, int x, int y, int width, int height, Consumer<T> consumer) {
        this.options = options;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.consumer = consumer;
        this.placeholder = placeholder;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.hover == 0 ? Colors.DARKER_YELLOW : Colors.DARK_YELLOW);
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(Colors.DARKER_BLUE);
        g.drawString(this.selected != null ? this.options.get(this.selected) : this.placeholder, this.x + 10, this.y + this.height / 2 + 5);

        if (this.open) {
            var index = new AtomicInteger(1);

            this.options.forEach((key, value) -> {
                var offset = this.y + index.get() * this.height;

                g.setColor(this.hover == index.get() ? Color.LIGHT_GRAY : Color.WHITE);
                g.fillRect(this.x, offset, this.width, this.height);
                g.setColor(Colors.DARKER_BLUE);
                g.drawString(value, this.x + 10, offset + this.height / 2 + 5);

                index.getAndIncrement();
            });
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

        if (this.hover > 0 && this.options.size() > this.hover - 1) {
            var keys = List.copyOf(this.options.keySet());

            this.consumer.accept(keys.get(this.hover - 1));
            this.selected = keys.get(this.hover - 1);
        }
    }

    @Override
    public boolean blocking() {
        return this.open;
    }
}
