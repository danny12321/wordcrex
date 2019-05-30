package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ManagerController;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagerView extends View<ManagerController> {


    private List<ButtonWidget> roleButtons = new ArrayList<>();

    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private int scroll;

    private int offset = Main.TASKBAR_SIZE + 48;
    private int height = 96;

    private Main main;

    public ManagerView(ManagerController controller, Main main) {
        super(controller);
        this.main = main;
    }



    @Override
    public void draw(Graphics2D g) {
        List<User> users = this.controller.getUsersWithRoles();

        for (int i = 0; i < users.size(); i++) {
            int ypos = this.offset + this.height * i - this.scroll;
            //frame
            g.setColor(Colors.DARKERER_BLUE);
            g.fillRect(0,ypos,Main.FRAME_SIZE, this.height);
            //username
            g.setColor(Colors.DARKER_YELLOW);
            g.drawString( users.get(i).username, Main.TASKBAR_SIZE + 40, ypos + this.height / 2);
            //user icon
            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(20, ypos + 42/2, 42, 42);
            g.setColor(Colors.DARKER_BLUE);
            g.setFont(Fonts.BIG);
            StringUtil.drawCenteredString(g, 20, ypos + 42/2, 42,42, users.get(i).username.substring(0,1).toUpperCase());
            //set font back to normal
            g.setFont(Fonts.NORMAL);
            //create role buttons
            int index = 0;
            for(UserRole u : UserRole.values()) {
                if (users.get(i).roles.contains(u)) {
                    g.setColor(Colors.DARK_YELLOW);
                }
                else {
                    g.setColor(Colors.DARK_BLUE);
                }

                roleButtons.add(new ButtonWidget(u.role.substring(0,1).toUpperCase(),270 + (index  * 50), ypos + this.height / 4, 30, 30,Colors.DARK_BLUE,Color.WHITE,Colors.DARK_YELLOW,()-> System.out.println("Button")));
                //g.fillOval(270 + (index  * 50), ypos + this.height / 4, 30, 30);
                //g.setColor(Colors.DARKERER_BLUE);
                //StringUtil.drawCenteredString(g, 270 + (index  * 50), ypos + this.height / 4, 30, 30, u.role.substring(0,1).toUpperCase());
                index++;
            }
            for(ButtonWidget b : roleButtons){

            }
        }

        this.scrollbar.setHeight(users.size() * this.height + this.offset - Main.TASKBAR_SIZE);
    }

    @Override
    public void update() {
    }

    @Override
    public List<Widget> getChildren() {
        List<Widget> widgets = new ArrayList<>();
        widgets.addAll(List.of(
                this.scrollbar,
                new InputWidget("Gebruikersnaam", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, this.controller::searchUsersWithRoles)

        ));
        widgets.addAll(roleButtons);
        return widgets;
    }
}
