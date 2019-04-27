package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.util.List;

public class GameUI extends UI {
    private int scroll;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawString("Games", 100, 100 - this.scroll);
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            new ScrollUI(SwingView.SIZE * 2, (scroll) -> this.scroll = scroll)
        );
    }
}
