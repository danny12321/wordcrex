package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.LoginView;

public class LoginController extends Controller<User> {
    public LoginController(Main main, User model) {
        super(main, model);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new LoginView(this);
    }

    public boolean login(String username, String password) {
        this.replace((user) -> user.login(username, password));

        if (!this.getModel().isAuthenticated()) {
            return false;
        }

        this.main.openController(DashboardController.class);

        return true;
    }
}
