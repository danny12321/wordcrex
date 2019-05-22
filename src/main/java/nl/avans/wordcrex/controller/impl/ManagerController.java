package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ManagerView;

import java.util.Map;
import java.util.function.Function;

public class ManagerController extends Controller<User> {
    public ManagerController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ManagerView(this);
    }

    public Map<String, String> getUsers() {
        return null;
    }

    public String getDisplayName() {
        return this.getModel().getDisplayName();
    }

    public String getInitial() {
        return this.getModel().getInitial();
    }
}
