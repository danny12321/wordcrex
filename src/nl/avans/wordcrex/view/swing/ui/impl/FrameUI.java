package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.util.List;

public class FrameUI extends UI {
    private final SidebarUI sidebar = new SidebarUI();
    private final ButtonUI menu = new ButtonUI("+", 0, 0, GamePanel.TASKBAR_SIZE, GamePanel.TASKBAR_SIZE, this.sidebar::toggle);

    private GamePanel game;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
    }

    @Override
    public void draw(Graphics2D g) {
        this.menu.setText(this.sidebar.isOpen() ? "x" : "+");

        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(0, 0, SwingView.SIZE, GamePanel.TASKBAR_SIZE);
        g.setColor(Colors.DARK_BLUE);
        g.setFont(this.game.getBigFont());
        g.drawString("WORDCREX", 40, 25);
        g.setFont(this.game.getNormalFont());
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            this.sidebar,
            this.menu,
            new ButtonUI("x", SwingView.SIZE - GamePanel.TASKBAR_SIZE, 0, GamePanel.TASKBAR_SIZE, GamePanel.TASKBAR_SIZE, Color.RED, Colors.DARK_RED, Color.WHITE, this.game::close)
        );
    }

    @Override
    public boolean isBlocking() {
        return this.sidebar.isOpen();
    }
}
