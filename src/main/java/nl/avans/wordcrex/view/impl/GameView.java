package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.AbstractGameController;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.Playable;
import nl.avans.wordcrex.model.Played;
import nl.avans.wordcrex.model.TileType;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.particle.impl.ExplodeParticle;
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

public class GameView extends View<AbstractGameController> {
    private final DialogWidget dialog = new DialogWidget();
    private final ButtonWidget playButton = new ButtonWidget(Assets.read("next"), "spelen", 22, 76, 32, 32, this::play);
    private final ButtonWidget resignButton = new ButtonWidget(Assets.read("close"), "opgeven", 22, 172, 32, 32, this::resign);
    private final ButtonWidget resetButton = new ButtonWidget(Assets.read("reset"), "resetten", 22, 220, 32, 32, () -> {
        this.controller.setPlayed(List.of());
        this.updatePositions();
    });
    private final ButtonWidget shuffleButton = new ButtonWidget(Assets.read("shuffle"), "schudden", 22, 458, 32, 32, () -> {
        this.controller.shuffle();
        this.requestInitialize();
    });
    private final ButtonWidget winnerButton = new ButtonWidget(Assets.read("winner"), "winnende bord", 22, 76, 32, 32, () -> this.controller.setView(BoardView.WINNER));
    private final ButtonWidget hostButton = new ButtonWidget(Assets.read("host"), "bord van uitdager", 22, 124, 32, 32, () -> this.controller.setView(BoardView.HOST));
    private final ButtonWidget opponentButton = new ButtonWidget(Assets.read("opponent"), "bord van tegenstander", 22, 172, 32, 32, () -> this.controller.setView(BoardView.OPPONENT));
    private final ButtonWidget nextButton = new ButtonWidget(Assets.read("next"), "volgende ronde", 22, 356, 32, 32, this.controller::nextRound);
    private final ButtonWidget previousButton = new ButtonWidget(Assets.read("back"), "vorige ronde", 22, 404, 32, 32, this.controller::previousRound);
    private final List<DragWidget<Playable>> deck = new ArrayList<>();

    private int update;
    private List<Pair<Integer, Integer>> exploded = new ArrayList<>();
    private boolean hover;
    private int scoreWidth = 0;
    private int lastRound;

    public GameView(AbstractGameController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var metrics = g.getFontMetrics();
        var score = this.controller.getFormattedScore();
        var host = this.controller.getHost();
        var opponent = this.controller.getOpponent();

        this.scoreWidth = metrics.stringWidth(score) + 16;

        var offset = Main.FRAME_SIZE / 2 - this.scoreWidth / 2 - 8;

        g.setColor(this.hover ? Colors.DARKERER_BLUE : Colors.DARK_BLUE);
        g.fillRect(offset, 40, this.scoreWidth, 28);
        g.setColor(Color.WHITE);
        g.drawString(score, offset + 8, 60);
        g.drawString(host, offset - metrics.stringWidth(host) - 8, 60);
        g.drawString(opponent, offset + this.scoreWidth + 8, 60);

        var poolX = Main.FRAME_SIZE - 50;
        var poolY = 76;

        g.translate(poolX, poolY);
        this.drawTile(g, this.controller.getPlaceholder(), false);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, -24, 44, 72, String.valueOf(this.controller.getPool()));
        g.translate(-poolX, -poolY);

        StringUtil.drawCenteredString(g, poolX - 24, 150, 72, "ronde");
        StringUtil.drawCenteredString(g, poolX - 24, 170, 72, String.valueOf(this.controller.getRound().id));

        for (var tile : this.controller.getTiles()) {
            var position = this.getAbsolutePos(tile.x, tile.y);

            if (position == null) {
                continue;
            }

            g.setColor(this.controller.getTileColor(tile));
            g.fillRect(position.a + 1, position.b + 1, 22, 22);

            if (tile.type != TileType.NONE && tile.type != TileType.CENTER) {
                g.setColor(Color.WHITE);
                StringUtil.drawCenteredString(g, 52 + tile.x * 24, 52 + tile.y * 24, 24, 24, tile.multiplier + tile.type.type);
            }
        }

        var played = this.controller.getPlayed();

        if (played == null) {
            return;
        }

        this.controller.getBoard().forEach((b) -> {
            if (this.isExploded(b)) {
                return;
            }

            var position = this.getAbsolutePos(b.tile.x, b.tile.y);

            g.translate(position.a, position.b);
            this.drawTile(g, b.playable.character, played.stream().noneMatch((p) -> p.tile == b.tile));
            g.translate(-position.a, -position.b);
        });

        if (this.controller.canPlay()) {
            return;
        }

        var deck = this.controller.getDeck();

        for (var i = 0; i < deck.size(); i++) {
            var p = deck.get(i);

            if (played.stream().anyMatch((d) -> d.playable.id == p.id)) {
                continue;
            }

            var x = 142 + i * 34;
            var y = 462;

            g.translate(x, y);
            this.drawTile(g, p.character, false);
            g.translate(-x, -y);
        }
    }

    @Override
    public void drawForeground(Graphics2D g) {
        var played = this.controller.getPlayed();
        var score = this.controller.getScore();

        if (played == null || score <= 0) {
            return;
        }

        g.setFont(Fonts.SMALL);

        var last = played.stream()
            .max(Comparator.comparingInt((Played a) -> a.tile.x).thenComparingInt(a -> a.tile.y))
            .orElse(null);

        if (last == null) {
            return;
        }

        var abs = this.getAbsolutePos(last.tile.x, last.tile.y);
        var metrics = g.getFontMetrics();
        var width = metrics.stringWidth(String.valueOf(score)) + 6;

        g.setColor(Colors.DARK_YELLOW);
        g.fillRoundRect(abs.a + 14, abs.b + 14, width, 14, 6, 6);
        g.setColor(Colors.DARKER_BLUE);
        StringUtil.drawCenteredString(g, abs.a + 14, abs.b + 14, width, 14, String.valueOf(score));
        g.setFont(Fonts.NORMAL);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        if (this.update++ % (10 + Main.RANDOM.nextInt(40)) == 0 && this.controller.canPlay() && this.controller.hasWon()) {
            var board = this.controller.getBoard();

            if (board.size() > this.exploded.size()) {
                Played played;

                do {
                    played = board.get(Main.RANDOM.nextInt(board.size()));
                } while (this.isExploded(played));

                var abs = this.getAbsolutePos(played.tile.x, played.tile.y);

                for (var i = 0; i < 50; i++) {
                    var angle = Math.random() * 360.0f;
                    var multiplier = (float) Math.random() * 40.0f;
                    var velocityX = (float) Math.cos(angle) * multiplier;
                    var velocityY = (float) Math.sin(angle) * multiplier;

                    addParticle.accept(new ExplodeParticle(abs.a, abs.b, velocityX, velocityY, played.playable.character));
                }

                this.exploded.add(new Pair<>(played.tile.x, played.tile.y));
            }
        }

        var round = this.controller.getRound();
        var can = this.controller.isHost() ? round.hostTurn == null : round.opponentTurn == null;

        if (this.controller.canPlay()) {
            this.playButton.setEnabled(can && (this.controller.getPlayed().isEmpty() || this.controller.getScore() > 0));
            this.resignButton.setEnabled(can);
            this.resetButton.setEnabled(can);
            this.shuffleButton.setEnabled(can);
            this.deck.forEach((d) -> d.setEnabled(can));
        } else {
            this.winnerButton.setEnabled(this.controller.getView() != BoardView.WINNER);
            this.hostButton.setEnabled(this.controller.getView() != BoardView.HOST);
            this.opponentButton.setEnabled(this.controller.getView() != BoardView.OPPONENT);
            this.nextButton.setEnabled(this.controller.getRound().id < this.controller.getTotalRounds());
            this.previousButton.setEnabled(this.controller.getRound().id > 1);

            return;
        }

        var played = new ArrayList<Played>();

        for (var widget : this.deck) {
            var pos = widget.getPosition();

            if (pos == null) {
                continue;
            }

            played.add(new Played(widget.data, ListUtil.find(this.controller.getTiles(), (t) -> t.x == pos.a && t.y == pos.b)));
        }

        if (this.lastRound != round.id || this.deck.isEmpty()) {
            this.lastRound = round.id;
            this.controller.setPlayed(List.of());

            this.deck.forEach((d) -> d.setPosition(0, 0));
            this.requestInitialize();
        } else {
            this.controller.setPlayed(played);
        }
    }

    @Override
    public void mouseMove(int x, int y) {
        var offset = Main.FRAME_SIZE / 2 - this.scoreWidth / 2 - 8;

        this.hover = x > offset && x < offset + this.scoreWidth && y > 40 && y < 68;
    }

    @Override
    public void mouseClick(int x, int y) {
        if (!this.hover) {
            return;
        }

        this.controller.navigateHistory();
    }

    @Override
    public List<Widget> children() {
        var children = new ArrayList<Widget>();

        if (this.controller.canPlay()) {
            children.add(this.playButton);
            children.add(new ButtonWidget(Assets.read("messages"), "berichten", 22, 124, 32, 32, this.controller::navigateChat));
            children.add(this.resignButton);
            children.add(this.resetButton);
            children.add(this.shuffleButton);

            var deck = this.controller.getDeck();
            this.deck.clear();

            for (var i = 0; i < deck.size(); i++) {
                var playable = deck.get(i);

                this.deck.add(new DragWidget<>(playable, 142 + i * 34, 462, 24, 24, this.controller.canPlay(), (g, hover) -> this.drawTile(g, playable.character, hover), this::getAbsolutePos, this::getRelativePos, this::canDrop));
            }

            this.updatePositions();

            children.addAll(this.deck);
        } else {
            children.add(this.winnerButton);
            children.add(this.hostButton);
            children.add(this.opponentButton);
            children.add(this.nextButton);
            children.add(this.previousButton);
        }

        children.add(this.dialog);

        return children;
    }

    private boolean isExploded(Played played) {
        return ListUtil.find(this.exploded, (t) -> played.tile.x == t.a && played.tile.y == t.b) != null;
    }

    private void updatePositions() {
        var played = this.controller.getPlayed();

        for (var d : new ArrayList<>(this.deck)) {
            d.setPosition(0, 0);

            for (var p : played) {
                if (p.playable.id == d.data.id) {
                    d.setPosition(p.tile.x, p.tile.y);
                }
            }
        }
    }

    private void drawTile(Graphics2D g, Character character, boolean hover) {
        g.setColor(hover ? Color.LIGHT_GRAY : Color.WHITE);
        g.fillRoundRect(0, 0, 24, 24, 6, 6);
        g.setColor(Colors.DARK_BLUE);
        g.drawString(character.character, 3, 21);
        g.setFont(Fonts.SMALL);
        StringUtil.drawCenteredString(g, 12, 0, 12, 14, String.valueOf(character.value));
        g.setFont(Fonts.NORMAL);
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
        return this.isFree(x, y, this.controller.getPlayed()) && this.isFree(x, y, this.controller.getBoard());
    }

    private boolean isFree(int x, int y, List<Played> played) {
        if (played == null) {
            return true;
        }

        for (var p : played) {
            if (p.tile.x == x && p.tile.y == y) {
                return false;
            }
        }

        return true;
    }

    private void play() {
        var can = this.controller.getScore() > 0;

        if (!can && this.controller.getPlayed().isEmpty()) {
            this.dialog.show("Passen?", "Ja", "Nee", (positive) -> {
                if (!positive) {
                    return;
                }

                this.controller.play();
            });
        } else if (can) {
            this.controller.play();
        }
    }

    private void resign() {
        this.dialog.show("Opgeven?", "Ja", "Nee", (positive) -> {
            if (!positive) {
                return;
            }

            this.controller.resign();
        });
    }
}
