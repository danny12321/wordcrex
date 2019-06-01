package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.InviteController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.DropdownWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;

public class InviteView extends View<InviteController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private int scroll;

    private int offset = Main.TASKBAR_SIZE + 96 + 20;
    private int height = 96;

    private Pair<String, Boolean> hover;
    private Boolean disabled = false;

    public InviteView(InviteController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var users = this.controller.getUsers();

        for (int i = 0; i < users.size(); i++) {
            var user = users.get(i);
            var username = user.a;
            var ypos = this.offset + this.height * i - this.scroll;

            if (this.hover != null && this.hover.a.equals(username) && this.hover.b) {
                g.setColor(Colors.DARKERER_BLUE);
            } else {
                g.setColor(Colors.DARKER_BLUE);
            }

            g.fillRect(0, ypos, Main.FRAME_SIZE, this.height);

            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(Main.TASKBAR_SIZE, ypos + 27, 42, 42);
            g.setFont(Fonts.BIG);
            g.setColor(Colors.DARKER_BLUE);
            StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, ypos + 27, 42, 42, username.substring(0, 1).toUpperCase());

            g.setFont(Fonts.NORMAL);
            g.setColor(Color.WHITE);
            g.drawString(username, Main.TASKBAR_SIZE * 2 + 42, ypos + this.height / 2);

            if (!user.b) {
                g.setFont(Fonts.SMALL);
                g.setColor(Color.RED);
                g.drawString("Is al uitgenodigd of speel je al een spel mee", Main.TASKBAR_SIZE * 2 + 42, ypos + this.height / 2 + 20);
            }
        }

        this.scrollbar.setHeight(10 * this.height + this.offset - Main.TASKBAR_SIZE);
    }

    @Override
    public void update() {
    }

    @Override
    public void mouseMove(int x, int y) {
        this.hover = null;

        int i = (y - this.offset) / this.height;

        if (this.offset < y && this.controller.getUsers().size() > i && 0 <= i) {
            this.hover = this.controller.getUsers().get(i);
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.hover == null || this.disabled) {
            return;
        }

        this.controller.invite(this.hover);
    }

    @Override
    public List<Widget> getChildren() {
        var dictionaries = this.controller.getDictionaries();

        return List.of(
            this.scrollbar,
            new InputWidget("Zoek op gebruikersnaam", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, this.controller::findOpponents),
            new DropdownWidget<>(dictionaries, "Selecteer taal", 20, this.offset - 48 - 10, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 40, 48, this.controller::setDictionary, (open) -> this.disabled = open)
        );
    }
}
