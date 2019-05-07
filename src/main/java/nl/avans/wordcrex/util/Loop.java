package nl.avans.wordcrex.util;

import java.util.HashMap;
import java.util.Map;

public class Loop implements Runnable {
    private final Map<Double, Runnable> loops;

    private boolean running = true;

    public Loop(Map<Double, Runnable> loops) {
        this.loops = loops;
    }

    @Override
    public void run() {
        var last = System.nanoTime();
        var delta = new HashMap<Double, Double>();

        while (this.running) {
            var now = System.nanoTime();
            var diff = now - last;

            this.loops.forEach((key, value) -> {
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

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        this.running = false;
    }
}
