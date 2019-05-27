package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.DashboardController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Console;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardView extends View<DashboardController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);

    private int scroll;
    private int hover;

    public DashboardView(DashboardController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var games = this.controller.getGames().stream()
            .filter(this.controller::isVisible)
            .collect(Collectors.toList());
        var offset = 0;
        var height = 96;
        var count = 0;
        var last = "";

        if (games.isEmpty()) {
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, "No games");
        }

        for (var i = 0; i < games.size(); i++) {
            var game = games.get(i);
            var position = height * i + offset - this.scroll + Main.TASKBAR_SIZE;

            if (!game.state.state.equals(last)) {
                g.setColor(Colors.DARK_BLUE);
                g.fillRect(0, position, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 64);
                g.setColor(Colors.DARK_YELLOW);
                g.drawString(this.controller.getLabel(game), Main.TASKBAR_SIZE, position + 38);

                last = game.state.state;
                offset += 64;
                position += 64;
            }

            if (this.hover == game.id) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(0, position, Main.FRAME_SIZE - Main.TASKBAR_SIZE, height);
            }

            var other = this.controller.isCurrentUser(game.host) ? game.opponent : game.host;

            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(Main.TASKBAR_SIZE, position + 27, 42, 42);
            g.setFont(Fonts.BIG);
            g.setColor(Colors.DARKER_BLUE);
            StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, position + 27, 42, 42, other.username.substring(0, 1).toUpperCase());
            g.setFont(Fonts.NORMAL);

            g.setColor(Color.WHITE);
            g.drawString(other.username, Main.TASKBAR_SIZE * 2 + 42, position + 52);

            if (i < games.size() - 1 && games.get(i + 1).state.state.equals(last)) {
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

        var games = this.controller.getGames().stream()
            .filter(this.controller::isVisible)
            .collect(Collectors.toList());
        var offset = 0;
        var height = 96;
        var last = "";

        for (var i = 0; i < games.size(); i++) {
            var game = games.get(i);
            var position = height * i + offset - this.scroll + Main.TASKBAR_SIZE;

            if (!game.state.state.equals(last)) {
                last = game.state.state;
                offset += 64;
                position += 64;
            }

            if (y > position && y < position + height) {
                if (!this.controller.isSelectable(game)) {
                    break;
                }

                this.hover = game.id;

                break;
            }
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.hover == 0) {
            return;
        }

        this.controller.navigateGame(this.hover);
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.scrollbar,
            new ButtonWidget("Nieuw spel", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 72, this.controller::newGame)
        );
    }
}
