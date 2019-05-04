package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.model.update.MatchUpdate;
import nl.avans.wordcrex.model.update.ModelUpdate;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.ui.UI;

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
    private final Consumer<MatchUpdate> matchObserver = (update) -> {
        System.out.println(this.match.status);
        System.out.println(update.rounds.size());
    };

    private int id;
    private SwingController controller;
    private Match match;

    public IngameUI(int id) {
        this.id = id;
    }

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.controller = controller;

        this.controller.observe(this.modelObserver);
    }

    @Override
    public void cleanup() {
        this.controller.remove(this.modelObserver);
    }

    @Override
    public void draw(Graphics2D g) {
    }
}
