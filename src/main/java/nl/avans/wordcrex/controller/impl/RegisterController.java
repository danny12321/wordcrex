package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.RegisterView;

import java.util.function.Function;

public class RegisterController extends Controller<Wordcrex> {
    private String username;
    private String password;
    private boolean failed;

    public RegisterController(Main main, Function<Wordcrex, Wordcrex> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
    }

    @Override
    public View<? extends Controller<Wordcrex>> createView() {
        return new RegisterView(this);
    }

    public void register() {
        this.update((user) -> user.register(this.username, this.password));

        if (this.getModel().user == null) {
            this.failed = true;

            return;
        }

        this.main.openController(GamesController.class, (model) -> model.user);
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
        return StringUtil.isAuthInput(this.username) && StringUtil.isAuthInput(this.password);
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public void navigateBack() {
        this.main.popController();
    }
}
