package nl.avans.wordcrex.controller.swing;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.model.Model;
import nl.avans.wordcrex.model.Player;
import nl.avans.wordcrex.view.swing.SwingView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwingController implements Controller<SwingView>, Runnable {
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
        var loops = Map.<Double, Runnable>of(
            4.0d, this.model::poll,
            30.0d, this.view::update,
            60.0d, this.view::draw
        );

        var last = System.nanoTime();
        var delta = new HashMap<Double, Double>();

        while (this.running) {
            var now = System.nanoTime();
            var diff = now - last;

            loops.forEach((key, value) -> {
                var current = delta.getOrDefault(key, 0.0d);
                var next = current + diff / (1000000000.0d / key);

                if (next >= 1.0d) {
                    value.run();

                    next--;
                }

                delta.put(key, next);
            });

            last = now;
        }
    }

    public void stop() {
        this.running = false;
    }

    public boolean login(String username, String password) {
        return this.model.login(username, password);
    }

    public void logout() {
        this.model.logout();
    }

    public Player getPlayer() {
        return this.model.getPlayer();
    }

    public List<Match> getMatches() {
        return this.model.getMatches();
    }
}
