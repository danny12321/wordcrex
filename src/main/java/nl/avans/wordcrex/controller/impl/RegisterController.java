package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.RegisterView;

import java.util.function.Function;

public class RegisterController extends Controller<User> {
    private static final String REGEX = "^[a-zA-Z0-9]{5,25}$";

    private String username;
    private String password;
    private boolean failed;

    public RegisterController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new RegisterView(this);
    }

    public void register() {
        this.replace((user) -> user.register(this.username, this.password));

        if (!this.getModel().authenticated) {
            this.failed = true;

            return;
        }

        this.main.openController(DashboardController.class);
    }

    public void setUsername(String username) {
        this.username = username;
        this.failed = false;
    }

    public void setPassword(String password) {
        this.password = password;
        this.failed = false;
    }

    public boolean isValid() {
        return this.username.matches(RegisterController.REGEX) && this.password.matches(RegisterController.REGEX);
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public void navigateLogin() {
        this.main.openController(LoginController.class);
    }
}
