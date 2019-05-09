package nl.avans.wordcrex.view;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.widget.Widget;

public abstract class View<T extends Controller> extends Widget {
    protected final T controller;

    public View(T controller) {
        this.controller = controller;
    }
}
