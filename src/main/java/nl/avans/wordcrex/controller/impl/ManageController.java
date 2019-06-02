package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ManageView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ManageController extends Controller<User> {
    private List<User> users = new ArrayList<>();

    public ManageController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ManageView(this);
    }

    public void searchUsers(String username) {
        this.users = this.getModel().findChangeable(username);
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void navigateAccount(User user) {
        this.main.openController(AccountController.class, (model) -> user);
    }
}
