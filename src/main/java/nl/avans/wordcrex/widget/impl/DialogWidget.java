package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class DialogWidget extends Widget {
    private final ButtonWidget positiveButton = new ButtonWidget("", 160, 272, 192, 32, () -> this.click(true));
    private final ButtonWidget negativeButton = new ButtonWidget("", 160, 320, 192, 32, () -> this.click(false));

    private boolean visible;
    private String message;
    private String positive;
    private String negative;
    private Consumer<Boolean> callback;

    @Override
    public void draw(Graphics2D g) {
        this.positiveButton.setVisible(this.visible);
        this.negativeButton.setVisible(this.visible);

        if (!this.visible) {
            return;
        }

        this.positiveButton.setText(this.positive);
        this.negativeButton.setText(this.negative);

        g.setColor(Colors.OVERLAY);
        g.fillRect(0, 0, Main.FRAME_SIZE, Main.FRAME_SIZE);
        g.setColor(Colors.DARKER_BLUE);
        g.fillRect(128, 128, Main.FRAME_SIZE - 256, Main.FRAME_SIZE - 256);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 128, 208, Main.FRAME_SIZE - 256, this.message);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public boolean blocking() {
        return this.visible;
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.positiveButton,
            this.negativeButton
        );
    }

    private void click(boolean positive) {
        this.visible = false;
        this.callback.accept(positive);
    }

    public void show(String message, String positive, String negative, Consumer<Boolean> callback) {
        this.message = message;
        this.positive = positive;
        this.negative = negative;
        this.callback = callback;
        this.visible = true;
    }
}
