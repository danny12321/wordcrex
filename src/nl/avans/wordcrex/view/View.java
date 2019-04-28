package nl.avans.wordcrex.view;

import nl.avans.wordcrex.controller.Controller;

public interface View<C extends Controller<?>> {
    void initialize(C controller);

    void draw();

    void update();
}
