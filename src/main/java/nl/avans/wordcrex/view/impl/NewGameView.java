package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.DashboardController;
import nl.avans.wordcrex.controller.impl.NewGameController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Console;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;

public class NewGameView extends View<NewGameController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private String search;
    private int scroll;

    public NewGameView(NewGameController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
//        var friends = this.controller.get
        this.scrollbar.setHeight(800);
    }

    @Override
    public void update() {
    }

    @Override
    public java.util.List<Widget> getChildren() {

        return List.of(
                this.scrollbar,
                new InputWidget("Friends username", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, (value) -> this.controller.search(value))
        );
    }
}
