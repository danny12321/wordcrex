package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.Word;
import nl.avans.wordcrex.model.WordState;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ApproveView;

import java.util.List;
import java.util.function.Function;

public class ApproveController extends Controller<User> {
    public List<Word> words;

    public ApproveController(Main main, Function<User, User> fn) {
        super(main, fn);
        this.words = this.getModel().getPendingWords();
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ApproveView(this);
    }

    @Override
    public void poll() {
        super.poll();

        this.words = this.getModel().getPendingWords();
    }

    public void accept(Word word) {
        this.getModel().updateWord(word, WordState.ACCEPTED);
    }

    public void decline(Word word) {
        this.getModel().updateWord(word, WordState.REJECTED);
    }
}
