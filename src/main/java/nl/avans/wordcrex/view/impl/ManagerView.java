package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.AccountController;
import nl.avans.wordcrex.controller.impl.ManagerController;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ManagerView extends View<ManagerController> {

    private ListWidget<User> list;

    private Main main;

    private List<User> users;

    public ManagerView(ManagerController controller, Main main) {
        super(controller);
        this.main = main;
        this.list = new ListWidget<>(
            Main.TASKBAR_SIZE + 12,
            96,
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
                    g.setColor(user.roles.contains(UserRole.values()[i]) ? Colors.DARK_YELLOW : Colors.DARK_BLUE);
                    g.fillOval(270+ (i  * 50), 27, 30, 30);
                    g.setColor(Colors.DARKERER_BLUE);
                    StringUtil.drawCenteredString(g, 270 + (i  * 50), 27, 30, 30, UserRole.values()[i].role.substring(0,1).toUpperCase());
                }
            },
            (previous, next) -> null,
            (user) -> user.username,
            (user) -> true,
            (user) -> {
                this.controller.getCurrentUser().setCurrentUserBeingEdited(user);
                this.main.openController(AccountController.class);
            }
        );
    }

    @Override
    public void draw(Graphics2D g) {

    }

    @Override
    public void update(Consumer<Particle> addParticle) {
            this.list.setItems(users);
    }

    @Override
    public List<Widget> getChildren() {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(this.list);
        widgets.addAll(
            List.of(
                new InputWidget("Gebruikersnaam", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, (result) -> {
                    this.controller.searchUsersWithRoles(result);
                    this.users = this.controller.getUsersWithRoles();
                })
            )
        );
        return widgets;
    }
}
