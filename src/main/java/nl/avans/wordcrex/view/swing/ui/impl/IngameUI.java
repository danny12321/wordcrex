package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.update.MatchUpdate;
import nl.avans.wordcrex.model.update.ModelUpdate;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;
import java.util.function.Consumer;

public class IngameUI extends UI {
    private final Consumer<ModelUpdate> modelObserver = (update) -> {
        var next = update.matches.stream()
            .filter((m) -> m.id == this.id)
            .findFirst()
            .orElse(null);

        if (next != this.match) {
            if (this.match != null) {
                this.match.remove(this.matchObserver);
            }

            this.match = next;

            if (this.match != null) {
                this.match.observe(this.matchObserver);
            }
        }
    };
    private final Consumer<MatchUpdate> matchObserver = (update) -> this.round = update.rounds.get(update.rounds.size() - 1);

    private int id;
    private GamePanel game;
    private SwingController controller;
    private Match match;
    private Round round;

    public IngameUI(int id) {
        this.id = id;
    }

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
        this.controller = controller;

        this.controller.observe(this.modelObserver);
    }

    @Override
    public void cleanup() {
        this.controller.remove(this.modelObserver);
        this.match.remove(this.matchObserver);
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.match == null || this.round == null) {
            return;
        }

        var metrics = g.getFontMetrics();
        var score = " 0 - 1000 ";
        var host = this.match.host.getDisplayName() + " ";
        var full = host + score + " " + this.match.opponent.getDisplayName();
        var offset = (SwingView.SIZE - metrics.stringWidth(full)) / 2;

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(offset + metrics.stringWidth(host), 40, metrics.stringWidth(score), 28);
        g.setColor(Color.WHITE);
        g.drawString(full, offset, 60);
        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(76, 79, 360, 360);

        for (var i = 0; i < this.round.deck.size(); i++) {
            var character = this.round.deck.get(i);

            g.setColor(Color.WHITE);
            g.fillRect(142 + i * 34, 464, 24, 24);
            g.setColor(Colors.DARK_BLUE);
            g.drawString(character.getText(), 145 + i * 34, 485);
            g.setFont(this.game.getSmallFont());
            g.drawString(String.valueOf(character.points), 157 + i * 34, 475);
            g.setFont(this.game.getNormalFont());
        }
    }
}
