package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.util.Console;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ManagerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ManagerController extends Controller<User> {

    private List<String> users = new ArrayList<>();

    public ManagerController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ManagerView(this);
    }

    public void search(String username) {
        printUserRoles();
        this.users = this.getModel().getUsers(username);
    }

    public List<String> getUsers() {
        return this.users;
    }
    
    public boolean hasRole(UserRole role){
        return this.getModel().roles.indexOf(role) != -1;
    }

    public void printUserRoles()
    {
        List<User> userRoles = this.getModel().getChangableUsers();
        System.out.println(userRoles.size());
        for (User u : userRoles)
        {
            System.out.println(u.username + " : " + u.roles.size());
            System.out.print(u.username + " : ");
            for(UserRole r : u.roles)
            {
                System.out.print(r.role + ", ");
            }
            System.out.println("");
        }
    }

}
