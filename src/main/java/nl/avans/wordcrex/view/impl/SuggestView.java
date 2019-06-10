package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.SuggestController;
import nl.avans.wordcrex.model.Word;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.DropdownWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ListWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class SuggestView extends View<SuggestController> {
    private final ListWidget<Word> list;
    private final ButtonWidget submitButton = new ButtonWidget("SUGGEREER", 0, 80, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 32, this.controller::suggest);

    public SuggestView(SuggestController controller) {
        super(controller);
        this.list = new ListWidget<>(
            80,
            96,
            "Geen suggesties",
            (word) -> word.word,
            (previous, next) -> previous == null || previous.dictionary != next.dictionary ? next.dictionary.name.toUpperCase() : null,
            (g, word) -> {
                g.setColor(Color.WHITE);
                g.drawString(word.word, Main.TASKBAR_SIZE, 44);
                g.setColor(Color.LIGHT_GRAY);
                g.setFont(Fonts.SMALL);
                g.drawString(this.controller.getLabel(word), Main.TASKBAR_SIZE, 60);
                g.setFont(Fonts.NORMAL);
            }
        );
    }

    @Override
    public void draw(Graphics2D g) {
    }

    @Override
    public void drawForeground(Graphics2D g) {
        if (!this.controller.hasFailed()) {
            return;
        }

        g.setColor(Colors.DARK_RED);
        g.fillRect(0, Main.FRAME_SIZE - 32, Main.FRAME_SIZE - 32, 32);
        g.setColor(Color.WHITE);
        StringUtil.drawCenteredString(g, 0, Main.FRAME_SIZE - 32, Main.FRAME_SIZE - 32, 32, "Dit woord kan je niet suggereren");
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getWords());
        this.submitButton.setEnabled(this.controller.isValid());
    }

    @Override
    public List<Widget> children() {
        return List.of(
            this.list,
            this.submitButton,
            new InputWidget("WOORD", 0, Main.TASKBAR_SIZE, 384, 48, this.controller::setSuggestion),
            new DropdownWidget<>(this.controller.getDictionaries(), "Taal", 384, Main.TASKBAR_SIZE, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 384, 48, this.controller::setDictionary)
        );
    }
}
