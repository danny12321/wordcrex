package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.AccountView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AccountController extends Controller<User> {
    private User user;

    public AccountController(Main main, Function<User, User> fn) {
        super(main, fn);

        this.user = this.getModel().getCurrentUserBeingEdited();
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new AccountView(this);
    }

    public void changePassword(String password) {
        this.user.changePassword(password);
    }

    public String getUsername() {
        return this.user.username;
    }

    public List<UserRole> getRoles() {
        return this.user.roles;
    }

    public void switchRole(UserRole role) {
        this.user.switchRole(role);
    }

    public boolean isAdmin() {
        return this.getModel().roles.contains(UserRole.ADMINISTRATOR);
    }
}
