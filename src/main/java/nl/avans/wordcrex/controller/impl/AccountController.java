package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.AccountView;

import java.util.List;
import java.util.function.Function;

public class AccountController extends Controller<User> {
    private User user;

    public AccountController(Main main, Function<User, User> fn) {
        super(main, Function.identity());
        this.user = fn.apply(this.getRoot()).initialize();
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new AccountView(this);
    }

    @Override
    public void poll() {
        this.user = this.user.poll();
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

    public boolean canChangeRole(UserRole role) {
        return !(this.getModel().roles.size() == 1 && this.getModel().hasRole(role));
    }

    public void toggleRole(UserRole role) {
        this.getModel().toggleRole(user, role);
    }

    public boolean canChangeRoles() {
        return this.getModel().hasRole(UserRole.ADMINISTRATOR);
    }
}
