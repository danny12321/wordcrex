package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ManageController;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ManageView extends View<ManageController> {
    private final ListWidget<User> list;

    public ManageView(ManageController controller) {
        super(controller);
        this.list = new ListWidget<>(
            48,
            96,
            "Geen gebruikers",
            (user) -> user.username,
            (previous, next) -> null,
            (g, user) -> {
                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(Main.TASKBAR_SIZE, 27, 42, 42);
                g.setFont(Fonts.BIG);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, 27, 42, 42, user.username.substring(0, 1).toUpperCase());
                g.setFont(Fonts.NORMAL);
                g.setColor(Color.WHITE);
                g.drawString(user.username, Main.TASKBAR_SIZE * 2 + 42, 52);

                for (var i = 0; i < UserRole.values().length; i++) {
                    g.setColor(user.hasRole(UserRole.values()[i]) ? Colors.DARK_YELLOW : Colors.DARK_BLUE);
                    g.fillRect(330 + (i * 30), 32, 30, 30);
                    g.setColor(Colors.DARKERER_BLUE);
                    StringUtil.drawCenteredString(g, 330 + (i * 30), 32, 30, 30, this.controller.getLabel(UserRole.values()[i]));
                }
            },
            (user) -> true,
            this.controller::navigateAccount
        );
    }

    @Override
    public void draw(Graphics2D g) {
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getUsers());
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.list,
            new InputWidget("GEBRUIKERSNAAM", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, this.controller::setFilter)
        );
    }
}
