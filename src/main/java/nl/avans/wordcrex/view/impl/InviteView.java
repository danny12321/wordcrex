package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.InviteController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ComboBoxWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.*;
import java.util.List;

public class InviteView extends View<InviteController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private int scroll;

    private int offset = Main.TASKBAR_SIZE + 96 + 20;
    private int height = 96;

    private String hover;
    private Boolean disabled = false;

    public InviteView(InviteController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        List<String> users = this.controller.getUsers();

        for (int i = 0; i < users.size(); i++) {
            String username = users.get(i);
            int ypos = this.offset + this.height * i - this.scroll;

            // background
            if(this.hover != null && this.hover.equals(username)) {
                g.setColor(Colors.DARKERER_BLUE);
            } else {
                g.setColor(Colors.DARKER_BLUE);
            }

            g.fillRect(0,ypos,Main.FRAME_SIZE, this.height);

            // First letter
            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(Main.TASKBAR_SIZE, ypos + 27, 42, 42);
            g.setFont(Fonts.BIG);
            g.setColor(Colors.DARKER_BLUE);
            StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, ypos + 27, 42, 42, username.substring(0, 1).toUpperCase());

            // name
            g.setFont(Fonts.NORMAL);
            g.setColor(Color.WHITE);
            g.drawString(username, Main.TASKBAR_SIZE * 2 + 42, ypos + this.height / 2);
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

        if(this.offset < y && this.controller.getUsers().size() > i && 0 <= i) {
            this.hover = this.controller.getUsers().get(i);
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        if (this.hover == null || this.disabled == true) {
            return;
        }

        this.controller.invite(this.hover);
    }

    @Override
    public java.util.List<Widget> getChildren() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("NL", "Nederlands");
        options.put("EN", "Engels");

        return List.of(
                this.scrollbar,
                new InputWidget("Zoek op gebruikersnaam", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, this.controller::search),
                new ComboBoxWidget(options, "Selecteer taal",20, this.offset - 48 - 10, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 40, 48, this.controller::setLanguageCode, (open) -> this.disabled = open)
        );
    }
}
