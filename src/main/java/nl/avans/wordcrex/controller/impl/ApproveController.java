package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ApproveView;

import java.util.List;
import java.util.function.Function;

public class ApproveController extends Controller<User> {
    public ApproveController(Main main, Function<Wordcrex, User> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update((model) -> model.poll(UserPoll.APPROVABLE));
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ApproveView(this);
    }

    public List<Word> getWords() {
        return this.getModel().approvable;
    }

    public void accept(Word word) {
        this.getModel().respondSuggestion(word, WordState.ACCEPTED);
    }

    public void decline(Word word) {
        this.getModel().respondSuggestion(word, WordState.REJECTED);
    }
}
