package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.MatchView;

import java.util.function.Function;

public class MatchController extends Controller<Match> {
    public MatchController(Main main, Function<User, Match> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<Match>> createView() {
        return new MatchView(this);
    }

    public String getStatus() {
        return this.getModel().status.name;
    }
}
