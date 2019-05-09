package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.controller.impl.MatchController;
import nl.avans.wordcrex.view.View;

import java.awt.*;

public class MatchView extends View<MatchController> {
    public MatchView(MatchController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawString(this.controller.getStatus(), 100, 100);
    }

    @Override
    public void update() {
    }
}
