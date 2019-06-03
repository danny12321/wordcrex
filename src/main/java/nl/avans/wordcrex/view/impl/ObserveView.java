package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ObserveController;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.GameState;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ObserveView extends View<ObserveController> {
    private final ListWidget<Game> list;
    private String search = "";

    public ObserveView(ObserveController controller) {
        super(controller);
        this.list = new ListWidget<>(
                47,
                96,
                (g, game) -> {

                    var host = game.host;
                    var opponent = game.opponent;

                    g.setFont(Fonts.NORMAL);
                    g.setColor(Color.WHITE);
                    g.drawString(host + " - " + opponent, Main.TASKBAR_SIZE , 44);
                    g.setFont(Fonts.SMALL);
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawString(game.dictionary.description, Main.TASKBAR_SIZE * 2 + 42, 60);
                    g.setFont(Fonts.NORMAL);

                    if (game.state == GameState.PLAYING) {
                        var metrics = g.getFontMetrics();
                        var score = " " + game.getHostScore() + " - " + game.getOpponentScore() + " ";
                        var width = metrics.stringWidth(score);

                        g.setColor(Colors.DARK_BLUE);
                        g.fillRect(450 - width, 34, width, 28);
                        g.setColor(Color.WHITE);
                        g.drawString(score, 450 - width, 54);
                    }
                },
                (previous, next) -> previous == null || previous.state != next.state ? this.controller.getLabel(next) : null,
                (game) -> String.valueOf(game.id),
                this.controller::isSelectable,
                (game) -> {

                    this.controller.navigateGame(game.id);
                }
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
                new InputWidget("ZOEKEN", 0, 30, 480, 48, (value) -> this.controller.setSearch(value))
        );
    }
}
