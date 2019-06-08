package nl.avans.wordcrex.controller;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.Persistable;
import nl.avans.wordcrex.view.View;

import java.util.function.Function;

public abstract class Controller<T extends Persistable> {
    protected final Main main;

    private Function<Wordcrex, T> fn;

    public Controller(Main main, Function<Wordcrex, T> fn) {
        this.main = main;
        this.fn = fn;
    }

    protected T getModel() {
        return this.fn.apply(this.main.getModel());
    }

    protected Wordcrex getRoot() {
        return this.main.getModel();
    }

    public abstract void poll();

    protected void update(Function<T, T> mutate) {
        var model = this.getModel();
        var next = mutate.apply(model);

        if (next == null || model == next) {
            return;
        }

        this.main.updateModel(next);
    }

    public abstract View<? extends Controller<T>> createView();
}
