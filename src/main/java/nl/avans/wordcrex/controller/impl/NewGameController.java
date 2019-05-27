package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.NewGameView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NewGameController extends Controller<User> {
    private List<String> users = new ArrayList<>();
    private String languageCode;

    public NewGameController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new NewGameView(this);
    }

    public void search(String username) {
        this.users = this.getModel().getUsers(username);
    }

    public List<String> getUsers() {
        return this.users;
    }

    public void invite(String username) {

        if(languageCode.isEmpty()) {
            System.out.println("Selecteer een taal");
            return;
        }

        this.getModel().sendInvite(username, languageCode);
        this.main.openController(DashboardController.class);
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
