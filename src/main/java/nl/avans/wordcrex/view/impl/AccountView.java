package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.AccountController;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AccountView extends View<AccountController> {
    private final int gap = 16;
    private final int header = 32;
    private final int section = 96;
    private final int circle = 30;

    private InputWidget input = new InputWidget("Verander Wachtwoord", this.gap, Main.TASKBAR_SIZE + this.section + this.section / 2, Main.FRAME_SIZE - 64 - this.gap * 3, 32, this.controller::setPassword);
    private final ButtonWidget changeButton = new ButtonWidget("+", Main.FRAME_SIZE - 64 - this.gap, Main.TASKBAR_SIZE + this.section + this.section / 2, 64, 32, this::changePassword);

    public AccountView(AccountController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var textHeight = g.getFontMetrics().getHeight();

        this.changeButton.setEnabled(this.controller.isValid());

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.TASKBAR_SIZE, Main.FRAME_SIZE, this.header);
        g.setColor(Colors.DARK_YELLOW);
        g.drawString("Gebruikersnaam", this.gap, Main.TASKBAR_SIZE + textHeight);
        g.setColor(Color.WHITE);
        g.drawString(this.controller.getUsername(), this.gap, Main.TASKBAR_SIZE + this.section / 2 + textHeight);

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.TASKBAR_SIZE + this.section, Main.FRAME_SIZE, this.header);
        g.setColor(Colors.DARK_YELLOW);
        g.drawString("Verander Wachtwoord", this.gap, Main.TASKBAR_SIZE + this.section + textHeight);

        var roles = this.controller.getRoles();

        if (this.controller.canChangeRoles()) {
            g.setColor(Colors.DARK_BLUE);
            g.fillRect(0, Main.TASKBAR_SIZE + (this.section * 2), Main.FRAME_SIZE, this.header);
            g.setColor(Colors.DARK_YELLOW);
            g.drawString("Rollen (Administrator)", this.gap, Main.TASKBAR_SIZE + (this.section * 2) + textHeight);

            for (var i = 0; i < UserRole.values().length; i++) {
                g.setColor(roles.contains(UserRole.values()[i]) ? Colors.DARK_YELLOW : Colors.DARK_BLUE);
                g.fillOval(this.gap, Main.TASKBAR_SIZE + (this.section * 2) + this.header + i * this.circle + (i + 1) * this.gap, this.circle, this.circle);
                g.setColor(Colors.DARKERER_BLUE);
                StringUtil.drawCenteredString(g, this.gap, Main.TASKBAR_SIZE + (this.section * 2) + this.header + i * this.circle + (i + 1) * this.gap, this.circle, this.circle, UserRole.values()[i].role.substring(0, 1).toUpperCase());
            }
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public List<Widget> getChildren() {
        var list = new ArrayList<Widget>();

        list.add(this.input);
        list.add(changeButton);

        if (this.controller.canChangeRoles()) {
            list.add(new ButtonWidget("Verander", this.gap * 2 + this.circle, Main.TASKBAR_SIZE + (this.section * 2) + this.header + this.gap, 96, 32, () -> this.controller.toggleRole(UserRole.PLAYER)));
            list.add(new ButtonWidget("Verander", this.gap * 2 + this.circle, Main.TASKBAR_SIZE + (this.section * 2) + this.header + this.circle + 2 * this.gap, 96, 32, () -> this.controller.toggleRole(UserRole.OBSERVER)));
            list.add(new ButtonWidget("Verander", this.gap * 2 + this.circle, Main.TASKBAR_SIZE + (this.section * 2) + this.header + (this.circle * 2) + 3 * this.gap, 96, 32, () -> this.controller.toggleRole(UserRole.MODERATOR)));
            list.add(new ButtonWidget("Verander", this.gap * 2 + this.circle, Main.TASKBAR_SIZE + (this.section * 2) + this.header + (this.circle * 3) + 4 * this.gap, 96, 32, () -> this.controller.toggleRole(UserRole.ADMINISTRATOR)));
        }

        return list;
    }

    private void changePassword() {
        this.controller.changePassword();
        this.input.clearInput();
    }
}
