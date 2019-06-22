package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.AccountView;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AccountController extends Controller<User> {
    private String password;

    public AccountController(Main main, Function<Wordcrex, User> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update((model) -> model.poll(null));
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new AccountView(this);
    }

    public String getLabel(UserRole role) {
        switch (role) {
            case PLAYER:
                return "Speler";
            case OBSERVER:
                return "Waarnemer";
            case MODERATOR:
                return "Controleur";
            case ADMINISTRATOR:
                return "Beheerder";
            default:
                return "?";
        }
    }

    public User getUser() {
        return this.getModel();
    }

    public boolean isAdministrator() {
        return this.getRoot().user.hasRole(UserRole.ADMINISTRATOR);
    }

    public boolean canClick(UserRole role) {
        if (!this.isAdministrator()) {
            return false;
        } else if (this.getUser().roles.size() <= 1) {
            return !this.getUser().hasRole(role);
        }

        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValid() {
        return StringUtil.isAuthInput(this.password);
    }

    public List<UserRole> getRoles() {
        return this.isAdministrator() ? Arrays.asList(UserRole.values()) : this.getUser().roles;
    }

    public void toggleRole(UserRole role) {
        this.getRoot().user.toggleRole(this.getUser(), role);
    }

    public void changePassword() {
        if (!this.isValid()) {
            return;
        }

        this.getUser().changePassword(this.password);
    }

    public boolean hasBackButton() {
        return this.main.hasPrevious();
    }

    public void navigateBack() {
        if (!this.hasBackButton()) {
            return;
        }

        this.main.popController();
    }
}
