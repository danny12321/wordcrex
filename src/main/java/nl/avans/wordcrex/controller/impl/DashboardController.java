package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Match;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.DashboardView;

import java.util.List;

public class DashboardController extends Controller<User> {
    public DashboardController(Main main, User model) {
        super(main, model);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new DashboardView(this);
    }

    public boolean isCurrentUser(User user) {
        return user.id == this.getModel().id;
    }

    public boolean canSelectMatch(Match match) {
        return match.status == Match.Status.PLAYING || (match.status == Match.Status.PENDING && !this.isCurrentUser(match.host));
    }

    public List<Match> getMatches() {
        return this.getModel().matches;
    }

    public void navigateMatch(int id) {
        this.main.openController(MatchController.class, StreamUtil.getModelProperty((user) -> user.matches, (match) -> match.id == id));
    }
}
