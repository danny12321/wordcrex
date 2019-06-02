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

    //private List<String> users = new ArrayList<>();
    private List<User> usersWithRoles = new ArrayList<>();

    private List<Pair<String, Boolean>> users = new ArrayList<>();

    public ManagerController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ManagerView(this, main);
    }

    public void search(String username) {
        this.users = this.getModel().findOpponents(username);
    }
    public void searchUsersWithRoles(String username){
        this.usersWithRoles = this.getModel().getChangableUsers(username);
    }


    public List<String> getUsers() {

        List<String> usernames = new ArrayList<>();

        for(Pair p : users){
            usernames.add(p.a.toString());
        }
        return usernames;

    }
    public List<User> getUsersWithRoles(){ return this.usersWithRoles; }
    
    public boolean hasRole(UserRole role){
        return this.getModel().roles.indexOf(role) != -1;
    }

    public List<User> getUserRoles(String name){
        return this.getModel().getChangableUsers(name);
    }


}
