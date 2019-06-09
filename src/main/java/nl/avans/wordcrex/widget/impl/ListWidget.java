package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListWidget<T> extends Widget {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private final int y;
    private final int height;
    private final Function<T, String> id;
    private final BiFunction<T, T, String> header;
    private final BiConsumer<Graphics2D, T> draw;
    private final Function<T, Boolean> clickable;
    private final Consumer<T> click;

    private List<T> items = new ArrayList<>();
    private int scroll;
    private String selected;

    public ListWidget(int y, int height, Function<T, String> id, BiFunction<T, T, String> header, BiConsumer<Graphics2D, T> draw) {
        this(y, height, id, header, draw, (item) -> false, null);
    }

    public ListWidget(int y, int height, Function<T, String> id, BiFunction<T, T, String> header, BiConsumer<Graphics2D, T> draw, Function<T, Boolean> clickable, Consumer<T> click) {
        this.y = y;
        this.height = height;
        this.id = id;
        this.header = header;
        this.draw = draw;
        this.clickable = clickable;
        this.click = click;
    }

    @Override
    public void draw(Graphics2D g) {
        var offset = this.y;
        var count = 0;

        for (var i = 0; i < this.items.size(); i++) {
            var item = this.items.get(i);
            var position = this.height * i + offset - this.scroll + Main.TASKBAR_SIZE;
            var header = this.getHeader(i);
            var active = this.id.apply(item).equals(this.selected);

            if (header != null) {
                g.setColor(Colors.DARK_BLUE);
                g.fillRect(0, position, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 64);
                g.setColor(Colors.DARK_YELLOW);
                g.drawString(header, Main.TASKBAR_SIZE, position + 38);

                offset += 64;
                position += 64;
            }

            if (active) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(0, position, Main.FRAME_SIZE - Main.TASKBAR_SIZE, this.height);
            }

            g.translate(0, position);
            this.draw.accept(g, item);
            g.translate(0, -position);

            if (i < this.items.size() - 1) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(Main.TASKBAR_SIZE * 2 + 42, position + this.height - 2, 268, 4);
            }

            count++;
        }

        this.scrollbar.setHeight(count * this.height + offset);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public void mouseMove(int x, int y) {
        this.selected = null;

        if (x > Main.FRAME_SIZE - Main.TASKBAR_SIZE || y < Main.TASKBAR_SIZE) {
            return;
        }

        var offset = this.y;

        for (var i = 0; i < this.items.size(); i++) {
            var item = this.items.get(i);
            var position = this.height * i + offset - this.scroll + Main.TASKBAR_SIZE;

            if (this.getHeader(i) != null) {
                offset += 64;
                position += 64;
            }

            if (y > position && y < position + this.height && this.clickable.apply(item)) {
                this.selected = this.id.apply(item);
            }
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.selected == null) {
            this.setFocus(false);
        } else {
            this.execute();
        }
    }

    @Override
    public void keyPress(int code, int modifiers) {
        if (!this.hasFocus()) {
            return;
        }

        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
            if (this.selected == null) {
                this.setFocus(true);

                return;
            }

            var offset = code == KeyEvent.VK_UP ? -1 : 1;
            var index = this.getSelectedIndex();

            do {
                index += offset;

                if (!this.isValidIndex(index)) {
                    return;
                }
            } while (!this.clickable.apply(this.items.get(index)));

            this.selected = this.id.apply(this.items.get(index));

            var height = this.height * index;

            for (var i = 0; i <= index; i++) {
                if (this.getHeader(i) != null && (i != index || index == this.items.size() - 1)) {
                    height += 64;
                }
            }

            if (offset == 1 && height - this.scroll > Main.FRAME_SIZE - this.height - this.y) {
                this.scrollbar.setOffset(height - (Main.FRAME_SIZE - Main.TASKBAR_SIZE - this.height - this.y));
            } else if (offset == -1 && height < this.y + this.scroll) {
                this.scrollbar.setOffset(height);
            }
        } else if (code == KeyEvent.VK_ENTER && this.selected != null) {
            this.execute();
        }
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.scrollbar
        );
    }

    @Override
    public boolean focusable() {
        return this.items.stream().anyMatch(this.clickable::apply);
    }

    @Override
    public void setFocus(boolean focus) {
        this.selected = focus ? this.id.apply(this.items.stream()
            .filter(this.clickable::apply)
            .findFirst()
            .orElseThrow()) : null;

        super.setFocus(focus);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    private void execute() {
        this.click.accept(this.items.stream()
            .filter((item) -> this.id.apply(item).equals(this.selected))
            .findFirst()
            .orElse(null));
    }

    private String getHeader(int i) {
        return this.header.apply(i > 0 ? this.items.get(i - 1) : null, this.items.get(i));
    }

    private int getSelectedIndex() {
        if (this.selected == null) {
            return 0;
        }

        for (var i = 0; i < this.items.size(); i++) {
            if (this.selected.equals(this.id.apply(this.items.get(i)))) {
                return i;
            }
        }

        return 0;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < this.items.size();
    }
}
