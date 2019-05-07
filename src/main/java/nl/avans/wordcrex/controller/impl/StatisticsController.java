package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.StatisticsView;

import java.util.Map;
import java.util.function.Function;

public class StatisticsController extends Controller<User> {
    public StatisticsController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new StatisticsView(this);
    }

    public Map<String, String> getStatistics() {
        return Map.of(
            "Games won", "20",
            "Games lost", "5",
            "Games tied", "2",
            "Games forfeited", "1",
            "Highest game score", "420",
            "Highest word score", "woordje (80)",
            "Highest game bonus", "0",
            "Total seven letter bonus", "0"
        );
    }

    public String getDisplayName() {
        return this.getModel().getDisplayName();
    }

    public String getInitial() {
        return this.getModel().getInitial();
    }
}
