package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.DashboardController;
import nl.avans.wordcrex.controller.impl.LoginController;
import nl.avans.wordcrex.controller.impl.ManagerController;
import nl.avans.wordcrex.controller.impl.StatisticsController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Console;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.DashboardView;
import nl.avans.wordcrex.view.impl.ManagerView;
import nl.avans.wordcrex.view.impl.StatisticsView;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.List;

public class SidebarWidget extends Widget {
    private final ButtonWidget games = new ButtonWidget("GAMES", 32, 64, 192, 32, () -> this.main.openController(DashboardController.class));
    private final ButtonWidget statistics = new ButtonWidget("STATISTICS", 32, 112, 192, 32, () -> this.main.openController(StatisticsController.class));
    private final ButtonWidget observe = new ButtonWidget("OBSERVE", 32, 160, 192, 32, Console.log("observe"));
    private final ButtonWidget words = new ButtonWidget("WORDS", 32, 208, 192, 32, Console.log("words"));
    private final ButtonWidget manage = new ButtonWidget("MANAGE", 32, 256, 192, 32, () -> this.main.openController(ManagerController.class));
    private final ButtonWidget logout = new ButtonWidget("LOGOUT", 32, 448, 192, 32, () -> this.main.openController(LoginController.class));

    private Main main;
    private boolean open;

    public SidebarWidget(Main main) {
        this.main = main;
    }

    @Override
    public void draw(Graphics2D g) {
        this.updateButton(this.games, DashboardView.class);
        this.updateButton(this.statistics, StatisticsView.class);
        this.updateButton(this.observe, null);
        this.updateButton(this.words, null);
        this.updateButton(this.manage, ManagerView.class);
        this.updateButton(this.logout, null);

        var width = Main.FRAME_SIZE / 2;

        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(this.open ? 0 : -width, Main.TASKBAR_SIZE, width, Main.FRAME_SIZE - Main.TASKBAR_SIZE);
    }

    @Override
    public void update() {
    }

    private void updateButton(ButtonWidget button, Class<? extends View<?>> cls) {
        button.setVisible(this.open);

        if (cls != null) {
            button.setEnabled(!this.main.isOpen(cls));
        }
    }

    @Override
    public List<Widget> getChildren() {
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
