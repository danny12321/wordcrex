package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.GameController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class GameView extends View<GameController> {
    private boolean hover;
    private int offset;
    private int hostWidth;
    private int scoreWidth;

    public GameView(GameController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var metrics = g.getFontMetrics();
        var score = " " + this.controller.getScore() + " ";
        var host = this.controller.getHostName() + " ";
        var full = host + score + " " + this.controller.getOpponentName();
        this.hostWidth = metrics.stringWidth(host);
        this.scoreWidth = metrics.stringWidth(score);
        this.offset = (Main.FRAME_SIZE - metrics.stringWidth(full)) / 2;

        g.setColor(this.hover ? Colors.DARKERER_BLUE : Colors.DARK_BLUE);
        g.fillRect(this.offset + this.hostWidth, 40, this.scoreWidth, 28);
        g.setColor(Color.WHITE);
        g.drawString(full, this.offset, 60);
        g.drawString(this.controller.getPoolSize() + " characters left", 32, 512);

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
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public void mouseMove(int x, int y) {
        var i = this.offset + this.hostWidth;

        this.hover = x > i && x < i + this.scoreWidth && y > 40 && y < 68;
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.hover) {
            this.controller.navigateHistory();
        }
    }

    @Override
    public List<Widget> children() {
        return List.of(
            new ButtonWidget("CHAT", 6, 52 + 24, 64, 32, this.controller::navigateChat)
        );
    }
}
