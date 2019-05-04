package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.util.StringUtil;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class DialogUI extends UI {
    private final ButtonUI positiveButton = new ButtonUI("", 160, 272, 192, 32, () -> this.click(true));
    private final ButtonUI negativeButton = new ButtonUI("", 160, 320, 192, 32, () -> this.click(false));

    private boolean visible;
    private String message;
    private String positive;
    private String negative;
    private Consumer<Boolean> callback;

    @Override
    public void initialize(GamePanel game, SwingController controller) {
    }

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
        g.fillRect(0, 0, SwingView.SIZE, SwingView.SIZE);
        g.setColor(Colors.DARKER_BLUE);
        g.fillRect(128, 128, SwingView.SIZE - 256, SwingView.SIZE - 256);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 128, 208, SwingView.SIZE - 256, this.message);
    }

    @Override
    public boolean isBlocking() {
        return this.visible;
    }

    @Override
    public boolean forceTop() {
        return true;
    }

    @Override
    public List<UI> getChildren() {
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
