package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Dictionary;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.InviteView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InviteController extends Controller<User> {
    private List<Pair<String, Boolean>> opponents = new ArrayList<>();
    private Dictionary dictionary;

    public InviteController(Main main, Function<Wordcrex, User> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update((model) -> model.poll(null));
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new InviteView(this);
    }

    public Map<Dictionary, String> getDictionaries() {
        var dictionaries = this.getRoot().dictionaries;
        var map = new LinkedHashMap<Dictionary, String>();

        for (var dictionary : dictionaries) {
            map.put(dictionary, dictionary.name);
        }

        return map;
    }

    public void findOpponents(String username) {
        this.opponents = this.getModel().findOpponents(username);
    }

    public List<Pair<String, Boolean>> getOpponents() {
        return this.opponents;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public boolean canClick(Pair<String, Boolean> opponent) {
        return this.dictionary != null && opponent.b;
    }

    public void invite(Pair<String, Boolean> opponent) {
        if (!this.canClick(opponent)) {
            return;
        }

        this.getModel().sendInvite(opponent.a, this.dictionary);
        this.main.popController();
    }

    public void navigateBack() {
        this.main.popController();
    }
}
