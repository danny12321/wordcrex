package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.DashboardController;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;

public class DashboardView extends View<DashboardController> {
    private final ListWidget<Game> list;

    public DashboardView(DashboardController controller) {
        super(controller);
        this.list = new ListWidget<>(
            72,
            96,
            (g, game) -> {
                var other = this.controller.isCurrentUser(game.host) ? game.opponent : game.host;

                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(Main.TASKBAR_SIZE, 27, 42, 42);
                g.setFont(Fonts.BIG);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, 27, 42, 42, other.getInitial());
                g.setFont(Fonts.NORMAL);
                g.setColor(Color.WHITE);
                g.drawString(other.username, Main.TASKBAR_SIZE * 2 + 42, 52);
            },
            (previous, next) -> previous == null || previous.state != next.state ? next.state.state : null,
            (game) -> game.id,
            (game) -> this.controller.navigateGame(game.id)
        );
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.controller.getGames().isEmpty()) {
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, "No games");
        }
    }

    @Override
    public void update() {
        this.list.setItems(this.controller.getGames());
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.list,
            new ButtonWidget("Nieuw spel", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 72, this.controller::navigateInvite)
        );
    }
}
