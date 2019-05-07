package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.MatchView;

public class MatchController extends Controller<Match> {
    public MatchController(Main main, Match model) {
        super(main, model);
    }

    @Override
    public View<? extends Controller<Match>> createView() {
        return new MatchView(this);
    }

    public String getStatus() {
        return this.getModel().status.name;
    }
}
