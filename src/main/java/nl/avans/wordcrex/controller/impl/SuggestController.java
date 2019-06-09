package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.SuggestView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SuggestController extends Controller<User> {
    private String suggestion = "";
    private Dictionary dictionary;
    private boolean failed;

    public SuggestController(Main main, Function<Wordcrex, User> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update((model) -> model.poll(UserPoll.WORDS));
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new SuggestView(this);
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
        this.failed = false;
    }

    public Map<Dictionary, String> getDictionaries() {
        var dictionaries = this.getRoot().dictionaries;
        var map = new LinkedHashMap<Dictionary, String>();

        for (var dictionary : dictionaries) {
            map.put(dictionary, dictionary.id);
        }

        return map;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public boolean isValid() {
        return this.dictionary != null && StringUtil.isWordInput(this.suggestion);
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public String getLabel(Word word) {
        switch (word.state) {
            case ACCEPTED:
                return "Geaccepteerd";
            case PENDING:
                return "In afwachting";
            case REJECTED:
                return "Afgewezen";
            default:
                return "?";
        }
    }

    public List<Word> getWords() {
        return this.getModel().words;
    }

    public void suggest() {
        if (!this.isValid()) {
            return;
        }

        this.failed = !this.getModel().suggestWord(this.suggestion, this.dictionary);
    }
}
