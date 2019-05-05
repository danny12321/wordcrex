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
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IngameUI extends UI {
    private final ButtonUI playButton = new ButtonUI(">", 22, 76, 32, 32, () -> this.round.playTurn(20, this.getPlaced()));
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
        this.characters = this.round.getCharacters().entrySet().stream()
            .map((entry) -> new Tile(this.game, entry.getValue(), entry.getKey()))
            .collect(Collectors.toList());
        this.deck.clear();

        for (var i = 0; i < this.round.deck.size(); i++) {
            this.deck.add(new Tile(this.game, this.round.deck.get(i), 142 + i * 34, 462));
        }
    };
    private final List<Tile> deck = new ArrayList<>();

    private int id;
    private GamePanel game;
    private SwingController controller;
    private Match match;
    private Round round;
    private List<Tile> characters;
    private Tile hover;
    private boolean dragging;
    private int mouseX;
    private int mouseY;
    private int offsetX;
    private int offsetY;
    private int target;

    public IngameUI(int id) {
        this.id = id;
    }

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
        this.controller = controller;

        this.controller.observe(this.modelObserver);
        this.playButton.setEnabled(false);
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
        g.fillRect(76, 76, 360, 360);

        if (this.dragging) {
            if (this.mouseX > 76 && this.mouseX < 436 && this.mouseY > 76 && this.mouseY < 436) {
                var size = 360 / GamePanel.GRID_SIZE;
                var x = Math.floorDiv(this.mouseX - 76, size);
                var y = Math.floorDiv(this.mouseY - 76, size);

                this.target = x * GamePanel.GRID_SIZE + y;

                g.setColor(Colors.DARK_BLUE);
                g.fillRect(76 + x * size, 76 + y * size, 24, 24);
            } else {
                this.target = -1;
            }
        }

        this.characters.forEach((tile) -> tile.draw(g));
        this.deck.forEach((tile) -> tile.draw(g));
    }

    @Override
    public int mouseMove(int x, int y) {
        for (var tile : this.deck) {
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
        this.mouseX = x;
        this.mouseY = y;

        if (this.dragging) {
            this.offsetX = x - this.hover.getX();
            this.offsetY = y - this.hover.getY();
        }
    }

    @Override
    public int mouseDrag(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;

        if (this.dragging) {
            this.hover.move(x - this.offsetX, y - this.offsetY);

            return Cursor.MOVE_CURSOR;
        }

        return Cursor.DEFAULT_CURSOR;
    }

    @Override
    public int mouseRelease(int x, int y) {
        if (this.dragging) {
            this.hover.setPosition(this.hasCharacter(this.target) ? -1 : this.target);
            this.checkWords();
        }

        this.dragging = false;

        return this.hover != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            this.playButton,
            new ButtonUI("o", 22, 124, 32, 32, () -> System.out.println("chat")),
            new ButtonUI("f", 22, 172, 32, 32, () -> System.out.println("forfeit"))
        );
    }

    private boolean hasCharacter(int position) {
        return Stream.concat(this.characters.stream(), this.deck.stream())
            .anyMatch((tile) -> tile.getPosition() == position);
    }

    private void checkWords() {
        var placed = this.getPlaced();
        var valid = !placed.isEmpty();

        this.playButton.setEnabled(valid);
    }

    private Map<Integer, Character> getPlaced() {
        return this.deck.stream()
            .filter((tile) -> tile.position != -1)
            .collect(Collectors.toMap((tile) -> tile.position, (tile) -> tile.character));
    }

    class Tile {
        private final GamePanel game;
        private final Character character;
        private final int initialX;
        private final int initialY;

        private int x;
        private int y;
        private boolean hover;
        private int position;
        private boolean enabled = true;

        Tile(GamePanel game, Character character, int position) {
            this(game, character, 0, 0);
            this.setPosition(position);
            this.enabled = false;
        }

        Tile(GamePanel game, Character character, int x, int y) {
            this.game = game;
            this.character = character;
            this.initialX = x;
            this.initialY = y;
            this.x = x;
            this.y = y;
            this.position = -1;
        }

        public void draw(Graphics2D g) {
            g.setColor(this.hover || !this.enabled ? Color.LIGHT_GRAY : Color.WHITE);
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

        public int getPosition() {
            return this.position;
        }

        public void setPosition(int position) {
            this.position = position;

            if (position == -1) {
                this.x = this.initialX;
                this.y = this.initialY;

                return;
            }

            var size = 360 / GamePanel.GRID_SIZE;
            var targetX = position / 15;
            var targetY = position % 15;

            this.x = 76 + targetX * size;
            this.y = 76 + targetY * size;
        }

        public void move(int x, int y) {
            this.x = Math.min(SwingView.SIZE - 24, Math.max(0, x));
            this.y = Math.min(SwingView.SIZE - 24, Math.max(GamePanel.TASKBAR_SIZE, y));
        }
    }
}
