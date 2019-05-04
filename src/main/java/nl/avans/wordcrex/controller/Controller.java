package nl.avans.wordcrex.controller;

import nl.avans.wordcrex.model.Model;
import nl.avans.wordcrex.view.View;

public interface Controller<V extends View<?>> {
    void initialize(V view, Model model);
}
