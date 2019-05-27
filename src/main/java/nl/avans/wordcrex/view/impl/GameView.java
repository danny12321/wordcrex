package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.GameController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;

import java.awt.*;

public class GameView extends View<GameController> {
    public GameView(GameController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var metrics = g.getFontMetrics();
        var score = " " + this.controller.getScore() + " ";
        var host = this.controller.getHostName() + " ";
        var full = host + score + " " + this.controller.getOpponentName();
        var offset = (Main.FRAME_SIZE - metrics.stringWidth(full)) / 2;

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(offset + metrics.stringWidth(host), 40, metrics.stringWidth(score), 28);
        g.setColor(Color.WHITE);
        g.drawString(full, offset, 60);

        for (var tile : this.controller.getTiles()) {
            g.setColor(Colors.DARKERER_BLUE);
            g.fillRect(52 + tile.x * 24, 52 + tile.y * 24, 24, 24);

            if (!tile.type.equals("--")) {
                g.setColor(Color.WHITE);
                StringUtil.drawCenteredString(g, 52 + tile.x * 24, 52 + tile.y * 24, 24, 24, tile.type);
            }
        }
    }

    @Override
    public void update() {
    }
}
