package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Dictionary;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.InviteView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InviteController extends Controller<User> {
    private List<Pair<String, Boolean>> users = new ArrayList<>();
    private Dictionary dictionary;

    public InviteController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new InviteView(this);
    }

    public Map<Dictionary, String> getDictionaries() {
        var dictionaries = new LinkedHashMap<Dictionary, String>();

        for (int i = 0; i < this.getModel().dictionaries.size(); i++) {
            dictionaries.put(this.getModel().dictionaries.get(i), this.getModel().dictionaries.get(i).description);
        }

        return dictionaries;
    }

    public void findOpponents(String username) {
        this.users = this.getModel().findOpponents(username);
    }

    public List<Pair<String, Boolean>> getUsers() {
        return this.users;
    }

    public void invite(Pair<String, Boolean> user) {
        if (this.dictionary == null) {
            System.out.println("Selecteer een taal");
            return;
        }

        if (!user.b) {
            System.out.println("Er word al een spel gespeeld met deze user");
            return;
        }

        this.getModel().sendInvite(user.a, this.dictionary);
        this.main.openController(DashboardController.class);
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }
}
