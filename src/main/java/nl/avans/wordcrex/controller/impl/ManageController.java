package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserPoll;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ManageView;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ManageController extends Controller<User> {
    private String filter = "";

    public ManageController(Main main, Function<Wordcrex, User> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update((model) -> model.poll(UserPoll.MANAGEABLE));
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ManageView(this);
    }

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase();
    }

    public List<User> getUsers() {
        return this.getModel().manageable.stream()
            .filter((user) -> user.username.toLowerCase().contains(this.filter))
            .collect(Collectors.toList());
    }

    public String getLabel(UserRole role) {
        switch (role) {
            case PLAYER:
                return "S";
            case OBSERVER:
                return "W";
            case MODERATOR:
                return "C";
            case ADMINISTRATOR:
                return "B";
            default:
                return "?";
        }
    }

    public void navigateAccount(User user) {
        this.main.openController(AccountController.class, StreamUtil.getModelProperty((model) -> model.user.manageable, (u) -> u.username.equals(user.username)));
    }
}
