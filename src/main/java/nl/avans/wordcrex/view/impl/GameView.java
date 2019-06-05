package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.GameController;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.Played;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.*;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.DragWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameView extends View<GameController> {
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
            var position = this.getTilePosition(tile.x, tile.y);

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
            var position = this.getTilePosition(played.x, played.y);

            g.translate(position.a, position.b);
            this.drawTile(g, played.character, true);
            g.translate(-position.a, -position.b);
        }

        var last = this.played.stream()
            .sorted(Comparator.comparingInt((a) -> a.x + a.y))
            .reduce((a, b) -> b)
            .orElse(null);

        if (last == null) {
            return;
        }

        var position = this.getTilePosition(last.x, last.y);

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
    public List<Widget> getChildren() {
        var list = new ArrayList<Widget>();
        var deck = this.controller.getRound().characters;

        if (this.controller.canPlay()) {
            list.add(new ButtonWidget(Asset.read("play"), 22, 76, 32, 32, Console.log("play")));
            list.add(new ButtonWidget(Asset.read("chat"), 22, 124, 32, 32, this.controller::navigateChat));
            list.add(new ButtonWidget(Asset.read("resign"), 22, 172, 32, 32, Console.log("resign")));
        } else {
            list.add(this.previousButton);
            list.add(this.nextButton);
        }

        for (var i = 0; i < deck.size(); i++) {
            var character = deck.get(i);

            list.add(new DragWidget(142 + i * 34, 462, 24, 24, (g, hover) -> this.drawTile(g, character, hover), this::dropTile, (pair, active) -> this.changeState(character, pair.a, pair.b, active)));
        }

        return list;
    }

    @Override
    public boolean shouldReinitialize() {
        var p = this.repaint;
        this.repaint = false;

        return p;
    }

    private Pair<Integer, Integer> dropTile(int x, int y) {
        for (var tile : this.controller.getTiles()) {
            var position = this.getTilePosition(tile.x, tile.y);

            if (x > position.a && x < position.a + 24 && y > position.b && y < position.b + 24) {
                return new Pair<>(position.a, position.b);
            }
        }

        return null;
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

    private void changeState(Character character, int x, int y, boolean active) {
        var coord = this.getTileCoord(x, y);

        if (active) {
            this.played.add(new Played(character, coord.a, coord.b));
        } else {
            this.played = this.played.stream()
                .filter((p) -> p.x != coord.a && p.y != coord.b)
                .collect(Collectors.toList());
        }

        this.score = this.controller.getNewScore(this.played);
    }

    private Pair<Integer, Integer> getTilePosition(int x, int y) {
        return new Pair<>(52 + x * 24, 52 + y * 24);
    }

    private Pair<Integer, Integer> getTileCoord(int x, int y) {
        return new Pair<>((x - 52) / 24, (y - 52) / 24);
    }
}
