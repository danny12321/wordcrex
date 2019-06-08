package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.LoginController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.particle.impl.TileParticle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class LoginView extends View<LoginController> {
    private final ButtonWidget submitButton = new ButtonWidget("LOG IN", 64, 312, 184, 48, this.controller::login);

    private int update;

    public LoginView(LoginController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.controller.hasFailed()) {
            return;
        }

        g.setColor(Colors.DARK_RED);
        g.fillRect(64, 360, 184, 32);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 64, 360, 184, 32, "ongeldig");
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.submitButton.setEnabled(this.controller.isValid());

        if (this.update++ % 5 != 0) {
            return;
        }

        addParticle.accept(new TileParticle(Main.RANDOM.nextInt(Main.FRAME_SIZE - 24), Main.RANDOM.nextFloat() - 0.5f, 10.0f + Main.RANDOM.nextFloat() * 5.0f));
    }

    @Override
    public List<Widget> children() {
        return List.of(
            new InputWidget("GEBRUIKERSNAAM", 64, 184, 384, 48, this.controller::setUsername),
            new InputWidget("WACHTWOORD", '*', 64, 248, 384, 48, this.controller::setPassword),
            this.submitButton,
            new ButtonWidget("REGISTREER", 264, 312, 184, 48, this.controller::navigateRegister)
        );
    }
}
