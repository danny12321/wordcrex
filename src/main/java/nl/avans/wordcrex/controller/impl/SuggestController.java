package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Dictionary;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.Word;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.SuggestView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SuggestController extends Controller<User> {
    private int page;
    private Map<String, List<Word>> words;
    private Dictionary dictionary;

    public SuggestController(Main main, Function<User, User> fn) {
        super(main, fn);
        this.setPage(0);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new SuggestView(this);
    }

    public Map<Dictionary, String> getDictionaries() {
        var dictionaries = new LinkedHashMap<Dictionary, String>();

        for (int i = 0; i < this.getModel().dictionaries.size(); i++) {
            dictionaries.put(this.getModel().dictionaries.get(i), this.getModel().dictionaries.get(i).code);
        }

        return dictionaries;
    }

    public boolean addWord(String word) {
        var result = this.getModel().suggestWord(word, this.dictionary);
        this.setPage(this.page);

        return result;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setPage(int page) {
        this.page = page;
        this.words = this.getModel().getSuggested(this.page);
    }

    public int getPage() {
        return this.page;
    }

    public Map<String, List<Word>> getWords() {
        return this.words;
    }
}
