package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.NewGameController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;

public class NewGameView extends View<NewGameController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private int scroll;

    private int offset = Main.TASKBAR_SIZE + 48;
    private int height = 96;

    public NewGameView(NewGameController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        List<String> users = this.controller.getUsers();

        for (int i = 0; i < users.size(); i++) {
            int ypos = this.offset + this.height * i - this.scroll;

            g.setColor(Colors.DARKERER_BLUE);
            g.fillRect(0,ypos,Main.FRAME_SIZE, this.height);

            g.setColor(Colors.DARKER_YELLOW);
            g.drawString("Name " + users.get(i), Main.TASKBAR_SIZE, ypos + this.height / 2);
        }

        this.scrollbar.setHeight(10 * this.height + this.offset - Main.TASKBAR_SIZE);
    }

    @Override
    public void update() {
    }

    @Override
    public java.util.List<Widget> getChildren() {

        return List.of(
                this.scrollbar,
                new InputWidget("Zoek op gebruikersnaam", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, this.controller::search)
        );
    }
}
