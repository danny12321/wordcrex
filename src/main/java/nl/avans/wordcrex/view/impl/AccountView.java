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
import java.util.List;
import java.util.function.Consumer;

public class AccountView extends View<AccountController> {
    private String passwordChange = "";
    private int gap = 16;

    private InputWidget input = new InputWidget("Verander Wachtwoord", this.gap, Main.TASKBAR_SIZE + this.gap + 128, Main.FRAME_SIZE - 64 - this.gap * 3, 32, (value) -> this.passwordChange = value);

    public AccountView(AccountController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var height = g.getFontMetrics().getHeight();

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.TASKBAR_SIZE, Main.FRAME_SIZE, 32);
        g.setColor(Colors.DARK_YELLOW);
        g.drawString("Gebruikersnaam", this.gap, Main.TASKBAR_SIZE + height);
        g.setColor(Color.WHITE);
        g.drawString(this.controller.getUsername(), this.gap, Main.TASKBAR_SIZE + 48 + height);

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.TASKBAR_SIZE + 96, Main.FRAME_SIZE, 32);
        g.setColor(Colors.DARK_YELLOW);
        g.drawString("Verander Wachtwoord", this.gap, Main.TASKBAR_SIZE + 96 + height);

        List<UserRole> roles = this.controller.getRoles();

        if(roles.contains(UserRole.ADMINISTRATOR)) {
            g.setColor(Colors.DARK_BLUE);
            g.fillRect(0, Main.TASKBAR_SIZE + 192, Main.FRAME_SIZE, 32);
            g.setColor(Colors.DARK_YELLOW);
            g.drawString("Rollen (Administrator)", this.gap, Main.TASKBAR_SIZE + 192 + height);

            for(var i = 0; i < UserRole.values().length; i++) {
                if (roles.contains(UserRole.values()[i])) {
                    g.setColor(Colors.DARK_YELLOW);
                } else {
                    g.setColor(Colors.DARK_BLUE);
                }

                g.fillOval(this.gap, Main.TASKBAR_SIZE + 224 + i * 30 + (i + 1) * this.gap, 30, 30);
                g.setColor(Colors.DARKERER_BLUE);
                StringUtil.drawCenteredString(g, this.gap, Main.TASKBAR_SIZE + 224 + i * 30 + (i + 1) * this.gap, 30, 30, UserRole.values()[i].role.substring(0,1).toUpperCase());
            }
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {

    }

    @Override
    public java.util.List<Widget> getChildren() {
        return List.of(
            this.input,
            new ButtonWidget("+", Main.FRAME_SIZE - 64 - this.gap, Main.TASKBAR_SIZE + this.gap + 128, 64, 32, () -> {
                this.controller.changePassword(this.passwordChange);
                this.input.clearInput();
            }),
            new ButtonWidget("Verander", this.gap * 2 + 30, Main.TASKBAR_SIZE + 224 + this.gap, 96, 32, () -> this.controller.switchRole(UserRole.PLAYER)),
            new ButtonWidget("Verander", this.gap * 2 + 30, Main.TASKBAR_SIZE + 224 + 30 + 2 * this.gap, 96, 32, () -> this.controller.switchRole(UserRole.OBSERVER)),
            new ButtonWidget("Verander", this.gap * 2 + 30, Main.TASKBAR_SIZE + 224 + 60 + 3 * this.gap, 96, 32, () -> this.controller.switchRole(UserRole.MODERATOR)),
            new ButtonWidget("Verander", this.gap * 2 + 30, Main.TASKBAR_SIZE + 224 + 90 + 4 * this.gap, 96, 32, () -> this.controller.switchRole(UserRole.ADMINISTRATOR))
        );
    }
}
