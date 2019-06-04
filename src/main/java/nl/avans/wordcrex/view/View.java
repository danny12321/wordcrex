package nl.avans.wordcrex.view;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.widget.Widget;

import java.util.stream.Collectors;

public abstract class View<T extends Controller> extends Widget {
    protected final T controller;

    public View(T controller) {
        this.controller = controller;
    }

    public void tabFocus(boolean reverse) {
        var children = this.getChildren().stream()
            .filter(Widget::canFocus)
            .collect(Collectors.toList());

        var updated = false;

        for (int i = 0; i < children.size(); i++) {
            var child = children.get(i);

            if (!child.hasFocus()) {
                continue;
            }

            if (reverse && i == 0) {
                children.get(children.size() - 1).requestFocus();
            } else if (reverse) {
                children.get(i - 1).requestFocus();
            } else if (i < children.size() - 1) {
                children.get(i + 1).requestFocus();
            } else {
                children.get(0).requestFocus();
            }

            updated = true;
        }

        if (!updated && !children.isEmpty()) {
            children.get(reverse ? children.size() - 1 : 0).requestFocus();
        }
    }
}
