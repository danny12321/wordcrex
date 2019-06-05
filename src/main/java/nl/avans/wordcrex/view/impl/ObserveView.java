package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ObserveController;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ObserveView extends View<ObserveController> {
    private final ListWidget<Game> list;

    public ObserveView(ObserveController controller) {
        super(controller);
        this.list = new ListWidget<>(
            47,
            96,
            (g, game) -> {
                var metrics = g.getFontMetrics();
                var score = " " + game.getLastRound().hostScore + " - " + game.getLastRound().opponentScore + " ";
                var width = metrics.stringWidth(score);

                g.setFont(Fonts.NORMAL);
                g.setColor(Color.WHITE);
                g.drawString(game.host, Main.TASKBAR_SIZE, 36);
                g.setFont(Fonts.SMALL);
                g.setColor(Color.LIGHT_GRAY);
                g.drawString("tegen", Main.TASKBAR_SIZE, 52);
                g.setFont(Fonts.NORMAL);
                g.setColor(Color.WHITE);
                g.drawString(game.opponent, Main.TASKBAR_SIZE, 70);
                g.setColor(Colors.DARK_BLUE);
                g.fillRect(450 - width, 34, width, 28);
                g.setColor(Color.WHITE);
                g.drawString(score, 450 - width, 54);
            },
            (previous, next) -> previous == null || previous.state != next.state ? this.controller.getLabel(next) : null,
            (game) -> String.valueOf(game.id),
            (game) -> true,
            this.controller::navigateGame
        );
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.controller.getGames().isEmpty()) {
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, "Geen spellen of uitdagingen");
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getGames());
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.list,
            new InputWidget("ZOEKEN", 0, 30, 480, 48, this.controller::setSearch)
        );
    }
}
