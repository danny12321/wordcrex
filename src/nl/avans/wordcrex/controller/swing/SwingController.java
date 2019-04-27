package nl.avans.wordcrex.controller.swing;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Model;
import nl.avans.wordcrex.view.swing.SwingView;

public class SwingController implements Controller<SwingView>, Runnable {
    private static final double UPS = 30.0d;
    private static final double FPS = 60.0d;

    private boolean running = true;
    private SwingView view;
    private Model model;

    @Override
    public void initialize(SwingView view, Model model) {
        this.view = view;
        this.model = model;

        new Thread(this).start();

        view.setVisible(true);
    }

    @Override
    public void run() {
        var last = System.nanoTime();
        var updateDelta = 0.0d;
        var frameDelta = 0.0d;
        var upns = 1000000000.0d / SwingController.UPS;
        var fpns = 1000000000.0d / SwingController.FPS;

        while (this.running) {
            var now = System.nanoTime();

            updateDelta += (now - last) / upns;
            frameDelta += (now - last) / fpns;
            last = now;

            if (updateDelta >= 1.0d) {
                this.model.update();
                this.view.update();

                updateDelta--;
            }

            if (frameDelta >= 1.0d) {
                this.view.draw();

                frameDelta--;
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
