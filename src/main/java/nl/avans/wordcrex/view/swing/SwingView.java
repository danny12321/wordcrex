package nl.avans.wordcrex.view.swing;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.View;

import javax.swing.*;

public class SwingView extends JFrame implements View<SwingController> {
    public static final int SIZE = 512;

    private GamePanel panel;

    @Override
    public void initialize(SwingController controller) {
        this.setTitle("Wordcrex");
        this.setSize(SwingView.SIZE, SwingView.SIZE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.add(this.panel = new GamePanel(this, controller));
    }

    @Override
    public void draw() {
        this.panel.repaint();
    }

    @Override
    public void update() {
        this.panel.update();
    }
}
