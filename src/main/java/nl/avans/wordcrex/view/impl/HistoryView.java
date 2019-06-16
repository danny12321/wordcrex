package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.HistoryController;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.Turn;
import nl.avans.wordcrex.model.TurnAction;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HistoryView extends View<HistoryController> {
    private final ListWidget<Round> list;

    public HistoryView(HistoryController controller) {
        super(controller);
        this.list = new ListWidget<>(
            128,
            128,
            "Geen rondes",
            (round) -> String.valueOf(round.id),
            (previous, next) -> "RONDE " + next.id + " - " + next.deck.stream().map((c) -> c.character.character).collect(Collectors.joining()),
            (g, round) -> {
                this.drawScore(g, round, round.hostTurn, 8, this.controller.getHost());
                this.drawScore(g, round, round.opponentTurn, 56, this.controller.getOpponent());
            }
        );
    }

    @Override
    public void draw(Graphics2D g) {
    }

    @Override
    public void drawForeground(Graphics2D g) {
        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 128);
        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(Main.TASKBAR_SIZE * 2 + 42, 156, 268, 4);

        this.drawUser(g, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 4 - 21, 0, this.controller.getHost(), this.controller.getHostScore());
        this.drawUser(g, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 4 * 3 - 21, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 2, this.controller.getOpponent(), this.controller.getOpponentScore());
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getRounds());
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.list
        );
    }

    private void drawUser(Graphics2D g, int ovalX, int stringX, String username, int score) {
        g.setColor(Colors.DARK_YELLOW);
        g.fillOval(ovalX, 50, 42, 42);
        g.setFont(Fonts.BIG);
        g.setColor(Colors.DARKER_BLUE);
        StringUtil.drawCenteredString(g, ovalX, 50, 42, 42, username.substring(0, 1).toUpperCase());
        g.setFont(Fonts.NORMAL);
        g.setColor(this.controller.getWinner().equals(username) ? Colors.DARK_YELLOW : Color.WHITE);
        StringUtil.drawCenteredString(g, stringX, 120, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 2, username);
        g.setFont(Fonts.SMALL);
        g.setColor(Color.LIGHT_GRAY);
        StringUtil.drawCenteredString(g, stringX, 134, (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 2, String.valueOf(score));
        g.setFont(Fonts.NORMAL);
    }

    private void drawScore(Graphics2D g, Round round, Turn turn, int y, String username) {
        var metrics = g.getFontMetrics();
        var status = "-";
        var score = "";

        g.setFont(Fonts.SMALL);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(username, Main.TASKBAR_SIZE, y + 43);

        if (turn.action == TurnAction.PLAYED) {
            score = "+" + turn.score;

            if (turn.bonus > 0) {
                score += " (+" + turn.bonus + ")";
            }

            status = this.controller.getPlayedWord(round, turn);
        } else if (turn.action == TurnAction.PASSED) {
            score = "gepast";
        } else {
            score = "opgegeven";
        }

        var width = metrics.stringWidth(score) + 16;

        g.setColor(Color.WHITE);
        g.setFont(Fonts.NORMAL);
        g.drawString(status, Main.TASKBAR_SIZE, y + 31);
        g.setFont(Fonts.NORMAL);
        g.setColor(Colors.DARK_BLUE);
        g.fillRect(450 - width, y + 18, width, 28);
        g.setColor(Color.WHITE);
        g.drawString(score, 450 - width + 8, y + 38);
    }
}
