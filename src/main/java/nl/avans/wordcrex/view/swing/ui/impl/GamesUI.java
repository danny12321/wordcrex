package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;
import java.util.List;

public class GamesUI extends UI {
    private final ScrollUI scroller = new ScrollUI(SwingView.SIZE * 2, (scroll) -> this.scroll = scroll);
    private final DialogUI dialog = new DialogUI();

    private int scroll;
    private GamePanel game;
    private SwingController controller;
    private Match active;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
        this.controller = controller;
    }

    @Override
    public void draw(Graphics2D g) {
        var matches = this.controller.getMatches();
        var offset = 0;
        var height = 96;
        var count = 0;
        Match.Status last = null;

        for (var i = 0; i < matches.size(); i++) {
            var match = matches.get(i);
            var position = height * i + offset - this.scroll + GamePanel.TASKBAR_SIZE;

            if (match.status != last) {
                if (match.status.name.isEmpty()) {
                    break;
                }

                g.setColor(Colors.DARK_BLUE);
                g.fillRect(0, position, SwingView.SIZE - GamePanel.TASKBAR_SIZE, 64);
                g.setColor(Colors.DARK_YELLOW);
                g.drawString(match.status.name, GamePanel.TASKBAR_SIZE, position + 38);

                last = match.status;
                offset += 64;
                position += 64;
            }

            if (this.active == match) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(0, position, SwingView.SIZE - GamePanel.TASKBAR_SIZE, height);
            }

            var other = match.host == this.controller.getPlayer() ? match.opponent : match.host;

            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(GamePanel.TASKBAR_SIZE, position + 27, 42, 42);
            g.setFont(this.game.getBigFont());
            g.setColor(Colors.DARKER_BLUE);
            StringUtil.drawCenteredString(g, GamePanel.TASKBAR_SIZE, position + 27, 42, 42, other.getDisplayName().substring(0, 1).toUpperCase());
            g.setFont(this.game.getNormalFont());

            g.setColor(Color.WHITE);
            g.drawString((match.host == this.controller.getPlayer() ? "To " : "From ") + other.getDisplayName(), GamePanel.TASKBAR_SIZE * 2 + 42, position + 52);

            if (i < matches.size() - 1 && matches.get(i + 1).status == last) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(GamePanel.TASKBAR_SIZE * 2 + 42, position + height - 2, 268, 4);
            }

            count++;
        }

        this.scroller.setHeight(count * height + offset);
    }

    @Override
    public int mouseMove(int x, int y) {
        this.active = null;

        if (x >= SwingView.SIZE - GamePanel.TASKBAR_SIZE || y <= GamePanel.TASKBAR_SIZE) {
            return Cursor.DEFAULT_CURSOR;
        }

        var matches = this.controller.getMatches();
        var offset = 0;
        var height = 96;
        Match.Status last = null;

        for (var i = 0; i < matches.size(); i++) {
            var match = matches.get(i);
            var position = height * i + offset - this.scroll + GamePanel.TASKBAR_SIZE;

            if (match.status.name.isEmpty()) {
                break;
            } else if (match.status != last) {
                last = match.status;
                offset += 64;
                position += 64;
            }

            if (y > position && y < position + height) {
                if (!this.canSelect(match)) {
                    break;
                }

                this.active = match;

                return Cursor.HAND_CURSOR;
            }
        }

        return Cursor.DEFAULT_CURSOR;
    }

    private boolean canSelect(Match match) {
        return match.host != this.controller.getPlayer();
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.active != null) {
            if (this.active.status == Match.Status.PENDING) {
                this.dialog.show("Accept?", "Yes", "No", (positive) -> {
                    if (positive) {
                        this.active.setStatus(Match.Status.PLAYING);
                        this.game.openUI(new IngameUI(this.active));
                    } else {
                        this.active.setStatus(Match.Status.REJECTED);
                    }
                });

                return;
            }

            this.game.openUI(new IngameUI(this.active));
        }
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            this.scroller,
            this.dialog
        );
    }
}
