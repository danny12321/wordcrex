package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ManagerController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagerView extends View<ManagerController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);

    private int scroll;

    public ManagerView(ManagerController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.TASKBAR_SIZE - this.scroll, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 128);
        g.setColor(Colors.DARK_YELLOW);
        g.fillOval(218, 58 - this.scroll, 42, 42);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 0, 128 - this.scroll, Main.FRAME_SIZE - Main.TASKBAR_SIZE, this.controller.getDisplayName());
        g.setColor(Colors.DARKER_BLUE);
        g.setFont(Fonts.BIG);
        StringUtil.drawCenteredString(g, 218, 58 - this.scroll, 42, 42, this.controller.getInitial());
        g.setFont(Fonts.NORMAL);

        var metrics = g.getFontMetrics(g.getFont());
        var index = new AtomicInteger();
        var stats = this.controller.getStatistics();

        stats.forEach((key, value) -> {
            var offset = index.getAndIncrement() * 88 - this.scroll;

            g.setColor(Color.WHITE);
            g.drawString(key, Main.TASKBAR_SIZE, 202 + offset);
            g.drawString(value, Main.FRAME_SIZE - Main.TASKBAR_SIZE * 2 - metrics.stringWidth(value), 202 + offset);

            if (index.get() < stats.size()) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(Main.TASKBAR_SIZE * 2 + 42, 236 + offset, 268, 4);
            }
        });

        this.scrollbar.setHeight(stats.size() * 88 + 128);
    }

    @Override
    public void update() {
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.scrollbar
        );
    }
}
