package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.AccountController;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Assets;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class AccountView extends View<AccountController> {
    private final ListWidget<UserRole> list;
    private final InputWidget passwordInput = new InputWidget("NIEUW WACHTWOORD", '*', 0, 160, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 48, 48, this.controller::setPassword);
    private final ButtonWidget submitButton = new ButtonWidget(Assets.read("next"), null, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 40, 168, 32, 32, this::changePassword);

    public AccountView(AccountController controller) {
        super(controller);
        this.list = new ListWidget<>(
            176,
            64,
            "Geen rollen",
            (role) -> role.role,
            (previous, next) -> null,
            (g, role) -> {
                g.setColor(this.controller.getUser().hasRole(role) ? Color.WHITE : Color.RED);
                g.drawString(this.controller.getLabel(role), Main.TASKBAR_SIZE, 36);
            },
            this.controller::canClick,
            this.controller::toggleRole
        );
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, 160, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48);
    }

    @Override
    public void drawForeground(Graphics2D g) {
        var username = this.controller.getUser().username;

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 128);
        g.setColor(Colors.DARK_YELLOW);
        g.fillOval(218, 58, 42, 42);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 0, 128, Main.FRAME_SIZE - Main.TASKBAR_SIZE, username);
        g.setColor(Colors.DARKER_BLUE);
        g.setFont(Fonts.BIG);
        StringUtil.drawCenteredString(g, 218, 58, 42, 42, username.substring(0, 1).toUpperCase());
        g.setFont(Fonts.NORMAL);
        g.setColor(Colors.DARKER_BLUE);
        g.fillRect(Main.TASKBAR_SIZE * 2 + 42, 156, 268, 4);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getRoles());
        this.submitButton.setEnabled(this.controller.isValid());
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.list,
            this.passwordInput,
            this.submitButton
        );
    }

    private void changePassword() {
        this.controller.changePassword();
        this.passwordInput.clearInput();
    }
}
