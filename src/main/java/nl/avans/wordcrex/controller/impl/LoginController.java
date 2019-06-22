package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.LoginView;
import nl.avans.wordcrex.widget.impl.SidebarWidget;

import java.util.function.Function;

public class LoginController extends Controller<Wordcrex> {
    private String username = "";
    private String password = "";
    private boolean failed;

    public LoginController(Main main, Function<Wordcrex, Wordcrex> fn) {
        super(main, fn);
        this.update(Wordcrex::logout);
    }

    @Override
    public void poll() {
    }

    @Override
    public View<? extends Controller<Wordcrex>> createView() {
        return new LoginView(this);
    }

    public void login() {
        this.update((model) -> model.login(this.username, this.password));

        if (this.getModel().user == null) {
            this.failed = true;

            return;
        }

        for (var item : SidebarWidget.ITEMS) {
            if (item.role == null || this.getModel().user.hasRole(item.role)) {
                this.main.openController(item.controller, (model) -> model.user);

                return;
            }
        }
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
        this.main.pushController(RegisterController.class, Function.identity());
    }
}
