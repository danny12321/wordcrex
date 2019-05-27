package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.controller.impl.LoginController;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Console;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;

import java.awt.*;
import java.util.List;

public class LoginView extends View<LoginController> {
    private final ButtonWidget submitButton = new ButtonWidget("LOG IN", 64, 312, 184, 48, this.controller::login);

    public LoginView(LoginController controller) {
        super(controller);
        this.controller.logout();
    }

    @Override
    public void draw(Graphics2D g) {
        this.submitButton.setEnabled(this.controller.isValid());

        if (this.controller.hasFailed()) {
            g.setColor(Colors.DARK_RED);
            g.fillRect(64, 360, 184, 32);
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 64, 360, 184, 32, "invalid");
        }
    }

    @Override
    public void update() {
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            new InputWidget("USERNAME", 64, 184, 384, 48, this.controller::setUsername),
            new InputWidget("PASSWORD", '*', 64, 248, 384, 48, this.controller::setPassword),
            this.submitButton,
            new ButtonWidget("REGISTER", 264, 312, 184, 48, this.controller::navigateRegister)
        );
    }
}
