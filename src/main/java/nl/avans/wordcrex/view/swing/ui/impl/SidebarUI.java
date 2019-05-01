package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.util.List;

public class SidebarUI extends UI {
    private final ButtonUI games = new ButtonUI("GAMES", 32, 64, 192, 32, () -> this.game.openUI(new GamesUI()));
    private final ButtonUI statistics = new ButtonUI("STATISTICS", 32, 112, 192, 32, () -> this.game.openUI(new StatisticsUI()));
    private final ButtonUI observe = new ButtonUI("OBSERVE", 32, 160, 192, 32, () -> System.out.println("Observe"));
    private final ButtonUI words = new ButtonUI("WORDS", 32, 208, 192, 32, () -> System.out.println("Words"));
    private final ButtonUI manage = new ButtonUI("MANAGE", 32, 256, 192, 32, () -> System.out.println("Manage"));
    private final ButtonUI logout = new ButtonUI("LOGOUT", 32, 448, 192, 32, () -> {
        this.controller.logout();
        this.game.openUI(new LoginUI());
    });

    private GamePanel game;
    private SwingController controller;
    private boolean open;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
        this.controller = controller;
    }

    @Override
    public void draw(Graphics2D g) {
        this.updateButton(this.games, GamesUI.class);
        this.updateButton(this.statistics, StatisticsUI.class);
        this.updateButton(this.observe, null);
        this.updateButton(this.words, null);
        this.updateButton(this.manage, null);
        this.updateButton(this.logout, null);

        var width = SwingView.SIZE / 2;
        var target = this.open ? 0 : -width;

        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(target, GamePanel.TASKBAR_SIZE, width, SwingView.SIZE - GamePanel.TASKBAR_SIZE);
    }

    private void updateButton(ButtonUI button, Class<? extends UI> cls) {
        button.setVisible(this.open);

        if (cls != null) {
            button.setActive(!this.game.isOpen(cls));
        }
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            this.games,
            this.statistics,
            this.observe,
            this.words,
            this.manage,
            this.logout
        );
    }

    public void toggle() {
        this.open = !this.open;
    }

    public boolean isOpen() {
        return this.open;
    }
}
