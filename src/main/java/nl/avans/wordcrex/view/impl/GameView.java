package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.GameController;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.Letter;
import nl.avans.wordcrex.model.Played;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.*;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.DialogWidget;
import nl.avans.wordcrex.widget.impl.DragWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameView extends View<GameController> {
    private final DialogWidget dialog = new DialogWidget();
    private final ButtonWidget previousButton = new ButtonWidget("<", 22, 403, 32, 32, () -> {
        this.controller.previousRound();
        this.repaint = true;
    });
    private final ButtonWidget nextButton = new ButtonWidget(">", 456, 403, 32, 32, () -> {
        this.controller.nextRound();
        this.repaint = true;
    });

    private boolean hover;
    private int offset;
    private int hostWidth;
    private int scoreWidth;
    private List<Played> played = new ArrayList<>();
    private int score = 0;
    private boolean repaint = false;


    public GameView(GameController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var metrics = g.getFontMetrics();
        var score = " " + this.controller.getScore() + " ";
        var host = this.controller.getHostName() + " ";
        var full = host + score + " " + this.controller.getOpponentName();

        this.hostWidth = metrics.stringWidth(host);
        this.scoreWidth = metrics.stringWidth(score);
        this.offset = (Main.FRAME_SIZE - metrics.stringWidth(full)) / 2;

        g.setColor(this.hover ? Colors.DARKERER_BLUE : Colors.DARK_BLUE);
        g.fillRect(this.offset + this.hostWidth, 40, this.scoreWidth, 28);
        g.setColor(Color.WHITE);
        g.drawString(full, this.offset, 60);

        if (this.controller.canPlay()) {
            var x = Main.FRAME_SIZE - 50;
            var y = 76;

            g.translate(x, y);
            this.drawTile(g, this.controller.getPlaceholder(), false);
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, -24, 44, 72, String.valueOf(this.controller.getPoolSize()));
            g.translate(-x, -y);
        } else {
            StringUtil.drawCenteredString(g, 436, 140, 72, "RND");
            StringUtil.drawCenteredString(g, 436, 160, 72, String.valueOf(this.controller.getRound().round));
        }

        for (var tile : this.controller.getTiles()) {
            var position = this.getAbsolutePos(tile.x, tile.y);

            if (position == null) {
                continue;
            }

            switch (tile.type) {
                case "--":
                    g.setColor(Colors.DARKERER_BLUE);
                    break;
                case "*":
                    g.setColor(Colors.DARK_YELLOW);
                    break;
                case "2L":
                    g.setColor(Colors.DARK_CYAN);
                    break;
                case "4L":
                    g.setColor(Colors.DARKER_CYAN);
                    break;
                case "6L":
                    g.setColor(Colors.DARKERER_CYAN);
                    break;
                case "3W":
                    g.setColor(Colors.DARK_PURPLE);
                    break;
                case "4W":
                    g.setColor(Colors.DARKER_PURPLE);
                    break;
            }

            g.fillRect(position.a + 1, position.b + 1, 22, 22);

            if (!tile.type.equals("--") && !tile.type.equals("*")) {
                g.setColor(Color.WHITE);
                StringUtil.drawCenteredString(g, 52 + tile.x * 24, 52 + tile.y * 24, 24, 24, tile.type);
            }
        }

        if (this.controller.getTiles().isEmpty()) {
            return;
        }

        for (var played : this.controller.getRound().board) {
            var position = this.getAbsolutePos(played.x, played.y);

            g.translate(position.a, position.b);
            this.drawTile(g, played.letter.character, true);
            g.translate(-position.a, -position.b);
        }

        var last = this.played.stream()
            .max(Comparator.comparingInt((Played a) -> a.x).thenComparingInt(a -> a.y))
            .orElse(null);

        if (last == null || this.score <= 0) {
            return;
        }

        var position = this.getAbsolutePos(last.x, last.y);

        g.setColor(Colors.DARK_YELLOW);
        g.fillRect(position.a + 24, position.b + 10, 14, 14);
        g.setFont(Fonts.SMALL);
        g.setColor(Colors.DARK_BLUE);
        StringUtil.drawCenteredString(g, position.a + 24, position.b + 10, 14, 14, String.valueOf(this.score));
        g.setFont(Fonts.NORMAL);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.previousButton.setEnabled(this.controller.getRound().round > 1);
        this.nextButton.setEnabled(this.controller.getRound().round < this.controller.getTotalRounds());
    }

    @Override
    public void mouseMove(int x, int y) {
        if (!this.controller.canPlay()) {
            return;
        }

        var i = this.offset + this.hostWidth;

        this.hover = x > i && x < i + this.scoreWidth && y > 40 && y < 68;
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.hover) {
            this.controller.navigateHistory();
        }
    }

    @Override
    public List<Widget> children() {
        var list = new ArrayList<Widget>();
        var deck = this.controller.getRound().deck;

        if (this.controller.canPlay()) {
            list.add(new ButtonWidget(Asset.read("play"), 22, 76, 32, 32, this::playTurn));
            list.add(new ButtonWidget(Asset.read("chat"), 22, 124, 32, 32, this.controller::navigateChat));
            list.add(new ButtonWidget(Asset.read("resign"), 22, 172, 32, 32, this::resign));
        } else {
            list.add(this.previousButton);
            list.add(this.nextButton);
        }

        for (var i = 0; i < deck.size(); i++) {
            var letter = deck.get(i);

            list.add(new DragWidget(142 + i * 34, 462, 24, 24, this.controller.canPlay(), (g, hover) -> this.drawTile(g, letter.character, hover), this::getAbsolutePos, this::getRelativePos, this::canDrop, (pair, active) -> this.changeState(letter, pair.a, pair.b, active)));
        }

        list.add(this.dialog);

        return list;
    }

    @Override
    public boolean shouldReinitialize() {
        var p = this.repaint;
        this.repaint = false;

        return p;
    }

    private void drawTile(Graphics2D g, Character character, boolean hover) {
        g.setColor(hover ? Color.LIGHT_GRAY : Color.WHITE);
        g.fillRect(0, 0, 24, 24);
        g.setColor(Colors.DARK_BLUE);
        g.drawString(character.character, 3, 21);
        g.setFont(Fonts.SMALL);
        g.drawString(String.valueOf(character.value), 15, 11);
        g.setFont(Fonts.NORMAL);
    }

    private void changeState(Letter letter, int x, int y, boolean active) {
        if (active) {
            this.played.add(new Played(letter, x, y));
        } else {
            this.played = this.played.stream()
                .filter((p) -> p.x != x || p.y != y)
                .collect(Collectors.toList());
        }

        this.score = this.controller.getNewScore(this.played);
    }

    private Pair<Integer, Integer> getAbsolutePos(int x, int y) {
        var size = Math.sqrt(this.controller.getTiles().size());

        if (x <= 0 || x > size || y <= 0 || y > size) {
            return null;
        }

        return new Pair<>(52 + x * 24, 52 + y * 24);
    }

    private Pair<Integer, Integer> getRelativePos(int x, int y) {
        var size = Math.sqrt(this.controller.getTiles().size()) + 1;

        if (x < 52 + 24 || x >= 52 + size * 24 || y < 52 + 24 || y >= 52 + size * 24) {
            return null;
        }

        var relativeX = (x - 52) / 24;
        var relativeY = (y - 52) / 24;

        return new Pair<>(relativeX, relativeY);
    }

    private boolean canDrop(int x, int y) {
        return this.isFree(x, y, this.played) && this.isFree(x, y, this.controller.getRound().board);
    }

    private boolean isFree(int x, int y, List<Played> played) {
        for (var p : played) {
            if (p.x == x && p.y == y) {
                return false;
            }
        }

        return true;
    }

    private void playTurn() {
        if (!this.played.isEmpty()) {
            this.controller.play(this.played);

            return;
        }

        this.dialog.show("Overslaan?", "JA", "NEE", (positive) -> {
            if (!positive) {
                return;
            }
            this.controller.play(this.played);
        });

    }

    private void resign(){
        this.dialog.show("Opgeven?", "JA", "NEE", (positive) -> {
            if (!positive) {
                return;
            }
            this.controller.resign();
        });

    }
}
