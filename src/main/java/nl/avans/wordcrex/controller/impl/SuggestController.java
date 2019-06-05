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
    private List<Word> words;
    private Dictionary dictionary;

    public SuggestController(Main main, Function<User, User> fn) {
        super(main, fn);
        this.words = this.getModel().getSuggested(0);
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
        this.words = this.getModel().getSuggested(0);

        return result;
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

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public boolean hasDictionary() {
        return this.dictionary != null;
    }

    public List<Word> getWords() {
        return this.words;
    }
}
