package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.RegisterView;

import java.util.function.Function;

public class RegisterController extends Controller<User> {
    private String username;
    private String password;

    public RegisterController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new RegisterView(this);
    }

    public boolean register() {
        this.replace((user) -> user.register(this.username, this.password));

        if (!this.getModel().authenticated) {
            return false;
        }

        this.main.openController(DashboardController.class);

        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValid() {
        return this.username.matches("^[a-zA-Z0-9]{5,25}$") && this.password.matches("^[a-zA-Z0-9]{5,25}$");
    }

    public void navigateLogin() {
        this.main.openController(LoginController.class);
    }
}
