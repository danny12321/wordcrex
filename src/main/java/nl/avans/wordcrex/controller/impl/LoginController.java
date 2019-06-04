package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.LoginView;
import nl.avans.wordcrex.widget.impl.SidebarWidget;

import java.util.function.Function;

public class LoginController extends Controller<User> {
    private String username;
    private String password;
    private boolean failed;

    public LoginController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new LoginView(this);
    }

    public void login() {
        this.replace((user) -> user.login(this.username, this.password));

        if (this.getModel().username.isEmpty()) {
            this.failed = true;

            return;
        }

        this.afterPoll(() -> {
            for (var item : SidebarWidget.ITEMS) {
                if (this.getModel().hasRole(item.role)) {
                    this.main.openController(item.controller);

                    return;
                }
            }
        });
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
        return !this.username.isEmpty() && !this.password.isEmpty();
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public void navigateRegister() {
        this.main.openController(RegisterController.class);
    }

    public void logout() {
        this.replace(User::logout);
    }
}
