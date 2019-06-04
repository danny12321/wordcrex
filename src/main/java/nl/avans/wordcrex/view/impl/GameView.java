package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.GameController;
import nl.avans.wordcrex.model.Tile;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.DragWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameView extends View<GameController> {
    private boolean hover;
    private int offset;
    private int hostWidth;
    private int scoreWidth;

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
        g.drawString(this.controller.getPoolSize() + " characters left", 32, 512);

        for (var tile : this.controller.getTiles()) {
            var position = this.getTilePosition(tile);

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
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
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
    public List<Widget> children() {
        var list = new ArrayList<Widget>();

        list.add(new ButtonWidget("CHAT", 6, 52 + 24, 64, 32, this.controller::navigateChat));

        var deck = this.controller.getDeck();
        for (var i = 0; i < deck.size(); i++) {
            var character = deck.get(i);

            list.add(new DragWidget(142 + i * 34, 462, 24, 24, (g, hover) -> {
                g.setColor(hover ? Color.LIGHT_GRAY : Color.WHITE);
                g.fillRect(0, 0, 24, 24);
                g.setColor(Colors.DARK_BLUE);
                g.drawString(character.character, 3, 21);
                g.setFont(Fonts.SMALL);
                g.drawString(String.valueOf(character.value), 15, 11);
                g.setFont(Fonts.NORMAL);
            }, this::dropTile));
        }

        return list;
    }

    private Pair<Integer, Integer> dropTile(int x, int y) {
        for (var tile : this.controller.getTiles()) {
            var position = this.getTilePosition(tile);

            if (x > position.a && x < position.a + 24 && y > position.b && y < position.b + 24) {
                return new Pair<>(position.a, position.b);
            }
        }

        return null;
    }

    private Pair<Integer, Integer> getTilePosition(Tile tile) {
        return new Pair<>(52 + tile.x * 24, 52 + tile.y * 24);
    }
}
