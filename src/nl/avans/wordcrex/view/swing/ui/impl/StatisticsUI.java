package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;
import java.util.List;

public class StatisticsUI extends UI {
    private int scroll;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, GamePanel.TASKBAR_SIZE - this.scroll, SwingView.SIZE - GamePanel.TASKBAR_SIZE, 128);
        g.setColor(Colors.DARK_YELLOW);
        g.fillOval(218, 58 - this.scroll, 42, 42);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 0, 128 - this.scroll, SwingView.SIZE - GamePanel.TASKBAR_SIZE, "nierennakker");
        g.setColor(Colors.DARKER_BLUE);
        g.setFont(GamePanel.BIG_FONT);
        g.drawString("N", 233, 89 - this.scroll);
        g.setFont(GamePanel.NORMAL_FONT);
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            new ScrollUI(SwingView.SIZE * 3, (scroll) -> this.scroll = scroll)
        );
    }
}
