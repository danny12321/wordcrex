package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ManagerController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagerView extends View<ManagerController> {
   // private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);

    private int scroll;

    public ManagerView(ManagerController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {

    }

    @Override
    public void update() {
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            //this.scrollbar
        );
    }
}
