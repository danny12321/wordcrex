package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.InviteView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public class InviteController extends Controller<User> {
    private List<HashMap<String, String>> users = new ArrayList<>();
    private String languageCode;

    public InviteController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new InviteView(this);
    }


    public LinkedHashMap<String, String> getDictionaries() {
        LinkedHashMap<String, String> dictionaries = new LinkedHashMap<>();

        for (int i = 0; i < this.getModel().dictionaries.size(); i++) {
            dictionaries.put(this.getModel().dictionaries.get(i).code, this.getModel().dictionaries.get(i).description);
        }

        return dictionaries;
    }

    public void search(String username) {
        this.users = this.getModel().getUsers(username);
    }

    public List<HashMap<String, String>> getUsers() {
        return this.users;
    }

    public void invite(HashMap<String, String> user) {

        if(languageCode.isEmpty()) {
            System.out.println("Selecteer een taal");
            return;
        }

        if(user.get("disabled") != null) {
            System.out.println("Er word al een spel gespeeld met deze user");
            return;
        }

        this.getModel().sendInvite(user.get("username"), languageCode);
        this.main.openController(DashboardController.class);
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
