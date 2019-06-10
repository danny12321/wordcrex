package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.InviteController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.DropdownWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class InviteView extends View<InviteController> {
    private final ListWidget<Pair<String, Boolean>> list;

    public InviteView(InviteController controller) {
        super(controller);
        this.list = new ListWidget<>(
            96,
            96,
            "Geen tegenstanders",
            (pair) -> pair.a,
            (previous, next) -> null,
            (g, pair) -> {
                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(Main.TASKBAR_SIZE, 27, 42, 42);
                g.setFont(Fonts.BIG);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, Main.TASKBAR_SIZE, 27, 42, 42, pair.a.substring(0, 1).toUpperCase());
                g.setFont(Fonts.NORMAL);
                g.setColor(Color.WHITE);
                g.drawString(pair.a, Main.TASKBAR_SIZE * 2 + 42, pair.b ? 52 : 44);

                if (!pair.b) {
                    g.setFont(Fonts.SMALL);
                    g.setColor(Color.RED);
                    g.drawString("Je hebt al een actief spel met deze speler", Main.TASKBAR_SIZE * 2 + 42, 60);
                    g.setFont(Fonts.NORMAL);
                }
            },
            this.controller::canClick,
            this.controller::invite
        );
    }

    @Override
    public void draw(Graphics2D g) {
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getOpponents());
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.list,
            new InputWidget("GEBRUIKERSNAAM", 0, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, this.controller::findOpponents),
            new DropdownWidget<>(this.controller.getDictionaries(), "Selecteer taal", 0, Main.TASKBAR_SIZE + 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48, this.controller::setDictionary)
        );
    }
}
