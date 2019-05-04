package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.model.Player;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatisticsUI extends UI {
    private final ScrollUI scroller = new ScrollUI(SwingView.SIZE * 2, (scroll) -> this.scroll = scroll);

    private int scroll;
    private GamePanel game;
    private Player player;
    private Map<String, String> statistics;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
        this.player = controller.getPlayer();
        this.statistics = this.player.getStatistics();
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, GamePanel.TASKBAR_SIZE - this.scroll, SwingView.SIZE - GamePanel.TASKBAR_SIZE, 128);
        g.setColor(Colors.DARK_YELLOW);
        g.fillOval(218, 58 - this.scroll, 42, 42);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 0, 128 - this.scroll, SwingView.SIZE - GamePanel.TASKBAR_SIZE, this.player.getDisplayName());
        g.setColor(Colors.DARKER_BLUE);
        g.setFont(this.game.getBigFont());
        StringUtil.drawCenteredString(g, 218, 58 - this.scroll, 42, 42, this.player.getInitial());
        g.setFont(this.game.getNormalFont());

        var metrics = g.getFontMetrics(g.getFont());
        var stats = new ArrayList<>(this.statistics.entrySet());

        for (var i = 0; i < stats.size(); i++) {
            var stat = stats.get(i);

            g.setColor(Color.WHITE);
            g.drawString(stat.getKey(), GamePanel.TASKBAR_SIZE, 202 + i * 88 - this.scroll);
            g.drawString(stat.getValue(), SwingView.SIZE - GamePanel.TASKBAR_SIZE * 2 - metrics.stringWidth(stat.getValue()), 202 + i * 88 - this.scroll);

            if (i < stats.size() - 1) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(GamePanel.TASKBAR_SIZE * 2 + 42, 236 + i * 88 - this.scroll, 268, 4);
            }
        }

        this.scroller.setHeight(stats.size() * 88 + 128);
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            this.scroller
        );
    }
}
