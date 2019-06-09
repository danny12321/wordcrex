package nl.avans.wordcrex.view;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;

public abstract class View<T extends Controller<?>> extends Widget {
    protected final T controller;

    private boolean request;

    public View(T controller) {
        this.controller = controller;
    }

    public void drawForeground(Graphics2D g) {
    }

    public boolean requestingInitialize() {
        var requested = this.request;

        this.request = false;

        return requested;
    }

    public void requestInitialize() {
        this.request = true;
    }
}
