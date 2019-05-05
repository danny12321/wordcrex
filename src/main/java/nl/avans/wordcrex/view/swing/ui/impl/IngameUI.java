package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.update.MatchUpdate;
import nl.avans.wordcrex.model.update.ModelUpdate;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
        this.round = update.rounds.get(update.rounds.size() - 1);
        this.tiles.clear();

        for (var i = 0; i < this.round.deck.size(); i++) {
            this.tiles.add(new Tile(this.game, this.round.deck.get(i), 142 + i * 34, 464));
        }
    };
    private final List<Tile> tiles = new ArrayList<>();

    private int id;
    private GamePanel game;
    private SwingController controller;
    private Match match;
    private Round round;
    private Tile hover;
    private boolean dragging;
    private int x;
    private int y;

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

        this.tiles.forEach((tile) -> tile.draw(g));
    }

    @Override
    public int mouseMove(int x, int y) {
        for (var tile : this.tiles) {
            if (tile.inside(x, y)) {
                this.hover = tile;

                return Cursor.HAND_CURSOR;
            }
        }

        this.hover = null;

        return Cursor.DEFAULT_CURSOR;
    }

    @Override
    public void mousePress(int x, int y) {
        this.dragging = this.hover != null;

        if (this.dragging) {
            this.x = x - this.hover.getX();
            this.y = y - this.hover.getY();
        }
    }

    @Override
    public int mouseDrag(int x, int y) {
        if (this.dragging) {
            this.hover.move(x - this.x, y - this.y);

            return Cursor.MOVE_CURSOR;
        }

        return Cursor.DEFAULT_CURSOR;
    }

    @Override
    public int mouseRelease(int x, int y) {
        this.dragging = false;

        return this.hover != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
    }

    class Tile {
        private final GamePanel game;
        private final Character character;

        private int x;
        private int y;
        private boolean hover;

        Tile(GamePanel game, Character character, int x, int y) {
            this.game = game;
            this.character = character;
            this.x = x;
            this.y = y;
        }

        public void draw(Graphics2D g) {
            g.setColor(this.hover ? Color.LIGHT_GRAY : Color.WHITE);
            g.fillRect(this.x, this.y, 24, 24);
            g.setColor(Colors.DARK_BLUE);
            g.drawString(this.character.getText(), this.x + 3, this.y + 21);
            g.setFont(this.game.getSmallFont());
            g.drawString(String.valueOf(this.character.points), this.x + 15, this.y + 11);
            g.setFont(this.game.getNormalFont());
        }

        public boolean inside(int x, int y) {
            return this.hover = x > this.x && x < this.x + 24 && y > this.y && y < this.y + 24;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public void move(int x, int y) {
            this.x = Math.min(SwingView.SIZE - 24, Math.max(0, x));
            this.y = Math.min(SwingView.SIZE - 24, Math.max(GamePanel.TASKBAR_SIZE, y));
        }
    }
}
