package nl.avans.wordcrex.controller;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.Pollable;
import nl.avans.wordcrex.view.View;

import java.util.function.Function;

public abstract class Controller<T extends Pollable<T>> {
    protected final Main main;

    private Function<User, T> fn;

    public Controller(Main main, Function<User, T> fn) {
        this.main = main;
        this.fn = fn;
        this.replace(Pollable::initialize);
    }

    protected T getModel() {
        return this.fn.apply(this.main.getModel());
    }

    public void poll() {
        this.replace(Pollable::poll);
    }

    protected void replace(Function<T, T> mutate) {
        var model = this.getModel();
        var next = mutate.apply(model);

        if (next == null || model == next) {
            return;
        }

        this.main.updateModel(next);
    }

    public abstract View<? extends Controller<T>> createView();
}
