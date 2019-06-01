package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ComboBoxWidget<T> extends Widget {
    private Map<T, String> options;
    private int x;
    private int y;
    private int width;
    private int height;
    private int hover = -1;
    private final Consumer<T> consumer;
    private final Consumer<Boolean> updateOpen;
    private T selected;
    private String placeholder;
    private int index = 0;

    private Boolean open = false;

    public ComboBoxWidget(Map<T, String> options, String placeholder, int x, int y, int width, int height, Consumer<T> consumer, Consumer<Boolean> updateOpen) {
        this.options = options;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.consumer = consumer;
        this.updateOpen = updateOpen;
        this.placeholder = placeholder;

        this.updateOpen.accept(this.open);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Colors.DARK_YELLOW);
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(Colors.DARKERER_BLUE);

        if (this.selected != null) {
            g.drawString(this.options.get(this.selected), this.x + 10, this.y + this.height / 2 + 5);
        } else {
            g.drawString(this.placeholder, this.x + 10, this.y + this.height / 2 + 5);
        }

        if (this.open) {
            this.index = 1;

            this.options.forEach((key, value) -> {
                var ypos = this.y + this.index * this.height;

                if (this.hover == this.index) {
                    g.setColor(Colors.DARKERER_BLUE);
                } else {
                    g.setColor(Color.white);
                }

                g.fillRect(this.x, ypos, this.width, this.height);

                if (this.hover == this.index) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.BLACK);
                }

                g.drawString(value, this.x + 10, ypos + this.height / 2 + 5);

                this.index = this.index + 1;
            });
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public void mouseMove(int x, int y) {
        this.hover = -1;

        if (x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height) {
            this.hover = 0;
        } else if (this.open && y > this.y + this.height) {
            this.hover = (y - this.y) / this.height;
        }
    }

    @Override
    public void mousePress(int x, int y) {
        this.open = this.hover == 0;

        if (this.hover > 0 && this.options.size() >= this.hover - 1) {
            var keys = List.copyOf(this.options.keySet());

            this.consumer.accept(keys.get(this.hover - 1));
            this.selected = keys.get(this.hover - 1);
        }

        this.updateOpen.accept(this.open);
    }
}
