package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.NewGameView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NewGameController extends Controller<User> {
    private List<String> users = new ArrayList<>();

    public NewGameController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new NewGameView(this);
    }

    public void search(String username) {
        this.users = this.getModel().getUsers(username);
    }

    public List<String> getUsers() {
        return this.users;
    }
}
