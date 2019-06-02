package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.util.Console;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ManagerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ManagerController extends Controller<User> {

    private List<User> usersWithRoles = new ArrayList<>();

    public ManagerController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ManagerView(this, main);
    }

    public void searchUsersWithRoles(String username){
        this.usersWithRoles = this.getModel().getChangeableUsers(username);
    }
    public User getCurrentUser(){
        return this.getModel();
    }

    public List<User> getUsersWithRoles(){ return this.usersWithRoles; }

}
