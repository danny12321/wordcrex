package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.DashboardController;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;

public class DashboardView extends View<DashboardController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);

    private int scroll;
    private int hover;

    public DashboardView(DashboardController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var matches = this.controller.getMatches();
        var offset = 0;
        var height = 96;
        var count = 0;
        var last = -1;

        if (matches.isEmpty()) {
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, "No matches");
        }

        for (var i = 0; i < matches.size(); i++) {
            var match = matches.get(i);
            var position = height * i + offset - this.scroll + Main.TASKBAR_SIZE;

            if (match.status.status != last) {
                if (match.status.name.isEmpty()) {
                    break;
                }

                g.setColor(Colors.DARK_BLUE);
                g.fillRect(0, position, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 64);
                g.setColor(Colors.DARK_YELLOW);
                g.drawString(match.status.name, Main.TASKBAR_SIZE, position + 38);

                last = match.status.status;
                offset += 64;
                position += 64;
            }

            if (this.hover == match.id) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(0, position, Main.FRAME_SIZE - Main.TASKBAR_SIZE, height);
            }

            var other = this.controller.isCurrentUser(match.host) ? match.opponent : match.host;

            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(Main.TASKBAR_SIZE, position + 27, 42, 42);
            g.setFont(Fonts.BIG);
            g.setColor(Colors.DARKER_BLUE);
            StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, position + 27, 42, 42, other.getDisplayName().substring(0, 1).toUpperCase());
            g.setFont(Fonts.NORMAL);

            g.setColor(Color.WHITE);
            g.drawString((this.controller.isCurrentUser(match.host) ? "To " : "From ") + other.getDisplayName(), Main.TASKBAR_SIZE * 2 + 42, position + 52);

            if (i < matches.size() - 1 && matches.get(i + 1).status.status == last) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(Main.TASKBAR_SIZE * 2 + 42, position + height - 2, 268, 4);
            }

            count++;
        }

        this.scrollbar.setHeight(count * height + offset);
    }

    @Override
    public void update() {
    }

    @Override
    public void mouseMove(int x, int y) {
        this.hover = 0;

        if (x > Main.FRAME_SIZE - Main.TASKBAR_SIZE || y < Main.TASKBAR_SIZE) {
            return;
        }

        var matches = this.controller.getMatches();
        var offset = 0;
        var height = 96;
        var last = -1;

        for (var i = 0; i < matches.size(); i++) {
            var match = matches.get(i);
            var position = height * i + offset - this.scroll + Main.TASKBAR_SIZE;

            if (match.status.name.isEmpty()) {
                break;
            } else if (match.status.status != last) {
                last = match.status.status;
                offset += 64;
                position += 64;
            }

            if (y > position && y < position + height) {
                if (!this.controller.canSelectMatch(match)) {
                    break;
                }

                this.hover = match.id;

                break;
            }
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        var match = this.controller.getMatches().stream()
            .filter((m) -> m.id == this.hover)
            .findFirst()
            .orElse(null);

        if (match == null || match.status != Match.Status.PLAYING) {
            return;
        }

        this.controller.navigateMatch(match);
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.scrollbar
        );
    }
}
