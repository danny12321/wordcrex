package nl.avans.wordcrex.controller;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.util.Pollable;
import nl.avans.wordcrex.view.View;

import java.util.function.Function;

public abstract class Controller<T extends Pollable<T>> {
    protected final Main main;

    private T model;

    public Controller(Main main, T model) {
        this.main = main;
        this.model = model;
    }

    protected T getModel() {
        return this.model;
    }

    public T poll() {
        this.replace(Pollable::poll);

        return this.model;
    }

    protected void replace(Function<T, T> mutate) {
        var next = mutate.apply(this.model);

        if (next == null) {
            return;
        }

        this.model = next;
    }

    public abstract View<? extends Controller<T>> createView();
}
