package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Player;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GamesUI extends UI {
    private final ScrollUI scroller = new ScrollUI(SwingView.SIZE * 2, (scroll) -> this.scroll = scroll);
    private final List<Game> games = new ArrayList<>();

    private int scroll;
    private GamePanel game;
    private Game active;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;

        var player1 = new Player("broodjeaap", null, null);
        var player2 = new Player("nierennakker", null, null);
        var player3 = new Player("gerda", null, null);

        this.games.add(new Game(player1, player2, Game.Status.PLAYING));
        this.games.add(new Game(player1, player3, Game.Status.PENDING));
        this.games.add(new Game(player3, player1, Game.Status.WAITING));
        this.games.add(new Game(player3, player2, Game.Status.WAITING));
        this.games.add(new Game(player2, player1, Game.Status.PENDING));

        this.games.sort(Comparator.comparingInt((g) -> g.getStatus().order));
    }

    @Override
    public void draw(Graphics2D g) {
        var offset = 0;
        var height = 96;
        Game.Status last = null;

        for (var i = 0; i < this.games.size(); i++) {
            var game = this.games.get(i);
            var position = height * i + offset - this.scroll + GamePanel.TASKBAR_SIZE;
            var status = game.getStatus();

            if (status != last) {
                if (status.name.isEmpty()) {
                    break;
                }

                g.setColor(Colors.DARK_BLUE);
                g.fillRect(0, position, SwingView.SIZE - GamePanel.TASKBAR_SIZE, 64);
                g.setColor(Colors.DARK_YELLOW);
                g.drawString(status.name, GamePanel.TASKBAR_SIZE, position + 38);

                last = status;
                offset += 64;
                position += 64;
            }

            if (this.active == game) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(0, position, SwingView.SIZE - GamePanel.TASKBAR_SIZE, height);
            }

            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(GamePanel.TASKBAR_SIZE, position + 27, 42, 42);
            g.setFont(this.game.getBigFont());
            g.setColor(Colors.DARKER_BLUE);
            StringUtil.drawCenteredString(g, GamePanel.TASKBAR_SIZE, position + 27, 42, 42, game.getOpponent().getDisplayName().substring(0, 1).toUpperCase());
            g.setFont(this.game.getNormalFont());

            g.setColor(Color.WHITE);
            g.drawString(game.getOpponent().getDisplayName(), GamePanel.TASKBAR_SIZE * 2 + 42, position + 52);

            if (i < this.games.size() - 1 && this.games.get(i + 1).getStatus() == last) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(GamePanel.TASKBAR_SIZE * 2 + 42, position + height - 2, 248, 4);
            }
        }

        this.scroller.setHeight(this.games.size() * height + offset);
    }

    @Override
    public int mouseMove(int x, int y) {
        this.active = null;

        if (x >= SwingView.SIZE - GamePanel.TASKBAR_SIZE || y <= GamePanel.TASKBAR_SIZE) {
            return Cursor.DEFAULT_CURSOR;
        }

        var offset = 0;
        var height = 96;
        Game.Status last = null;

        for (var i = 0; i < this.games.size(); i++) {
            var game = this.games.get(i);
            var position = height * i + offset - this.scroll + GamePanel.TASKBAR_SIZE;
            var status = game.getStatus();

            if (status != last) {
                last = status;
                offset += 64;
                position += 64;
            }

            if (y > position && y < position + height) {
                this.active = game;

                return Cursor.HAND_CURSOR;
            }
        }

        return Cursor.DEFAULT_CURSOR;
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.active != null) {
            System.out.println(this.active.getOpponent().getDisplayName());
        }
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            this.scroller
        );
    }
}
