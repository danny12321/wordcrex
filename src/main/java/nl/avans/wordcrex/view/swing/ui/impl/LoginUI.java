package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;
import java.util.List;

public class LoginUI extends UI {
    private GamePanel game;
    private SwingController controller;
    private String username;
    private String password;
    private boolean invalid;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
        this.controller = controller;
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.invalid) {
            g.setColor(Colors.DARK_RED);
            g.fillRect(64, 354, 384, 32);
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 64, 354, 384, 32, "invalid credentials");
        }
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            new InputUI("USERNAME", 64, 178, 384, 48, (value) -> this.username = value),
            new InputUI("PASSWORD", '*', 64, 242, 384, 48, (value) -> this.password = value),
            new ButtonUI("LOG IN", 64, 306, 384, 48, this::login)
        );
    }

    public void login() {
        this.invalid = false;

        if (this.controller.login(this.username, this.password)) {
            this.game.openUI(new GamesUI());
        } else {
            this.invalid = true;
        }
    }
}
