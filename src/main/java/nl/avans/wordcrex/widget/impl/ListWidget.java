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
    private final BiConsumer<Graphics2D, T> draw;
    private final BiFunction<T, T, String> header;
    private final Function<T, String> getId;
    private final Function<T, Boolean> canClick;
    private final Consumer<T> click;

    private List<T> items = new ArrayList<>();
    private int scroll;
    private String selected;
    private int selectedId;

    public ListWidget(int y, int height, BiConsumer<Graphics2D, T> draw, BiFunction<T, T, String> header, Function<T, String> getId, Function<T, Boolean> canClick, Consumer<T> click) {
        this.y = y;
        this.height = height;
        this.draw = draw;
        this.header = header;
        this.getId = getId;
        this.canClick = canClick;
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

            if (header != null) {
                g.setColor(Colors.DARK_BLUE);
                g.fillRect(0, position, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 64);
                g.setColor(Colors.DARK_YELLOW);
                g.drawString(header, Main.TASKBAR_SIZE, position + 38);

                offset += 64;
                position += 64;
            }

            if (this.getId.apply(item).equals(this.selected)) {
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

            if (y > position && y < position + this.height && this.canClick.apply(item)) {
                this.selected = this.getId.apply(item);
                this.selectedId = this.items.indexOf(item);
                break;
            }
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.selected == null) {
            return;
        }

        this.click.accept(this.items.stream()
            .filter((item) -> this.getId.apply(item).equals(this.selected))
            .findFirst()
            .orElse(null));
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.scrollbar
        );
    }

    @Override
    public void keyPress(int code, int modifiers) {

        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
            if (selected == null) {
                System.out.println("selected is empty");
                selected = this.getId.apply(items.get(0));
            } else {
                int way = code == KeyEvent.VK_UP ? -1 : 1;

                if ((this.selectedId + way) >= 0 && (this.selectedId + way) < items.size()) {
                    this.selectedId += way;
                    selected = this.getId.apply(items.get(this.selectedId));

                    int height = this.height * this.selectedId;

                    for(int i = 0; i <= this.selectedId; i++) {
                        if (this.getHeader(i) != null && (i != this.selectedId || this.selectedId == this.items.size() - 1)) {
                            height += 64;
                        }
                    }

                    if (way == 1 && height - this.scroll > Main.FRAME_SIZE - this.height - this.y) {
                        this.scrollbar.setOffset(height  - (Main.FRAME_SIZE - Main.TASKBAR_SIZE - this.height - this.y));
                    } else if (way == -1 && height < this.y + this.scroll) {
                        this.scrollbar.setOffset(height);
                    }

                }
            }
        }

        if(code == KeyEvent.VK_ENTER){
            this.click.accept(this.items.stream()
                    .filter((item) -> this.getId.apply(item).equals(this.selected))
                    .findFirst()
                    .orElse(null));
        }
    }

    @Override
    public boolean canFocus() {
        return true;
    }

    private String getHeader(int i) {
        var previous = i > 0 ? this.items.get(i - 1) : null;

        return this.header.apply(previous, this.items.get(i));
    }

    public int getScroll() {
        return this.scroll;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
