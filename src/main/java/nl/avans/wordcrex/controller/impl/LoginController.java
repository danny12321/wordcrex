package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.LoginView;

import java.util.function.Function;

public class LoginController extends Controller<User> {
    public LoginController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new LoginView(this);
    }

    public boolean login(String username, String password) {
        this.replace((user) -> user.login(username, password));

        if (!this.getModel().authenticated) {
            return false;
        }

        this.main.openController(DashboardController.class);

        return true;
    }

    public void navigateRegister(){
        this.main.openController(RegisterController.class);
    }

    public void logout() {
        this.replace(User::logout);
    }
}
