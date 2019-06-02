package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.HistoryController;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class HistoryView extends View<HistoryController> {
    private final ListWidget<Round> list;

    public HistoryView(HistoryController controller) {
        super(controller);
        this.list = new ListWidget<>(
            116,
            96,
            (g, round) -> {

            },
            (previous, next) -> "Ronde " + next.round + " - Hand: ",
            (round) -> String.valueOf(round.round),
            (round) -> false,
            (round) -> {}
        );

    }

    @Override
    public void draw(Graphics2D g) {
        this.drawPlayer(g, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 4 - 21, 0, this.controller.getHost(), String.valueOf(this.controller.getHostScore()));
        this.drawPlayer(g, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 4 * 3 - 21, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 2, this.controller.getOpponent(), String.valueOf(this.controller.getOpponentScore()));

    }

    private void drawPlayer(Graphics2D g, int ovalX, int stringX, String user, String score) {
        g.setColor(Colors.DARK_YELLOW);
        g.fillOval(ovalX, 48, 42, 42);
        g.setFont(Fonts.BIG);
        g.setColor(Colors.DARKER_BLUE);
        StringUtil.drawCenteredString(g, ovalX, 48, 42, 42, user.substring(0, 1).toUpperCase());
        g.setFont(Fonts.NORMAL);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, stringX, 112, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 2, user);
        g.setFont(Fonts.SMALL);
        g.setColor(Color.LIGHT_GRAY);
        StringUtil.drawCenteredString(g, stringX, 132, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 2, score);
        g.setFont(Fonts.NORMAL);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getRounds());
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.list,
            new ButtonWidget("<", 0, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, this.controller::navigateGame)
        );
    }
}
