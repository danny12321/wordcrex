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
    private static final String REGEX = "^[a-zA-Z0-9]{5,25}$";
    private String newPassword;

    public AccountController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new AccountView(this);
    }

    @Override
    public boolean replaceable() {
        return false;
    }

   public boolean isValid(){

       return this.newPassword.matches(AccountController.REGEX);
    }

    public void setPassword(String password) {
        this.newPassword = password;
    }

    public void changePassword(String password) {


        this.getModel().changePassword(password);
    }

    public String getUsername() {
        return this.getModel().username;
    }

    public List<UserRole> getRoles() {
        return this.getModel().roles;
    }

    public void toggleRole(UserRole role) {
        this.getRoot().toggleRole(this.getModel(), role);
    }

    public boolean canChangeRoles() {
        return this.getRoot().hasRole(UserRole.ADMINISTRATOR);
    }
}
