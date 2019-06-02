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


    private List<ButtonWidget> roleButtons = new ArrayList<>();

    private ListWidget<User> list;

    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private int scroll;

    private int offset = Main.TASKBAR_SIZE + 48;
    private int height = 96;

    private Main main;

    List<User> users;

    public ManagerView(ManagerController controller, Main main) {
        super(controller);
        this.main = main;
        this.list = new ListWidget<User>(
                Main.TASKBAR_SIZE + 12,
                96,
                (g, user) -> {
                    g.setColor(Colors.DARK_YELLOW);
                    g.fillOval(Main.TASKBAR_SIZE, 27, 42, 42);
                    g.setFont(Fonts.BIG);
                    g.setColor(Colors.DARKER_BLUE);
                    StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, 27, 42, 42, user.getInitial());
                    g.setFont(Fonts.NORMAL);
                    g.setColor(Color.WHITE);
                    g.drawString(user.username, Main.TASKBAR_SIZE * 2 + 42, 52);
                    int index = 0;
                    for(UserRole u : UserRole.values()) {
                        if (user.roles.contains(u)) {
                            g.setColor(Colors.DARK_YELLOW);
                        }
                        else {
                            g.setColor(Colors.DARK_BLUE);
                        }

                        //roleButtons.add(new ButtonWidget(u.role.substring(0,1).toUpperCase(),270 + (index  * 50), ypos + this.height / 4, 30, 30,Colors.DARK_BLUE,Color.WHITE,Colors.DARK_YELLOW,()-> System.out.println("Button")));
                        g.fillOval(270+ (index  * 50), 27, 30, 30);
                        g.setColor(Colors.DARKERER_BLUE);
                        StringUtil.drawCenteredString(g, 270 + (index  * 50), 27, 30, 30, u.role.substring(0,1).toUpperCase());
                        index++;
                    }
                },
                (previous, next) -> null,
                (user) -> user.username.hashCode(),
                (user) -> true,
                (user) -> {
                    this.controller.getCurrentUser().setCurrentUserBeingEdited(user);
                    this.main.openController(AccountController.class);
                }
        );
    }



    @Override
    public void draw(Graphics2D g) {
        this.scrollbar.setHeight(users.size() * this.height + this.offset - Main.TASKBAR_SIZE);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
            this.list.setItems(users);
    }

    @Override
    public List<Widget> getChildren() {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(this.list);
        widgets.addAll(List.of(
                this.scrollbar,
                new InputWidget("Gebruikersnaam", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, (result)->{  this.controller.searchUsersWithRoles(result);
                                                                                                                                                    this.users = this.controller.getUsersWithRoles();})

        ));
        //widgets.addAll(roleButtons);
        return widgets;
    }
}
