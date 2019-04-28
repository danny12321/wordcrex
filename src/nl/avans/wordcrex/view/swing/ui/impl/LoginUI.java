package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.util.List;

public class LoginUI extends UI {
    private GamePanel game;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;
    }

    @Override
    public void draw(Graphics2D g) {
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            new InputUI("USERNAME", 64, 178, 384, 48),
            new InputUI("PASSWORD", '*', 64, 242, 384, 48),
            new ButtonUI("LOG IN", 64, 306, 384, 48, () -> this.game.openUI(new GameUI()))
        );
    }
}
