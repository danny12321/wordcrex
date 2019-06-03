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
    private final ButtonWidget submitButton = new ButtonWidget("SUGGEREER", 0, 78, 480, 48, this::suggest);

    private String word = "";
    private boolean invalid;

    public SuggestView(SuggestController controller) {
        super(controller);
        this.list = new ListWidget<>(
                90,
                64,
                (g, word) -> {
                    g.setColor(Color.WHITE);
                    g.drawString(word.word, Main.TASKBAR_SIZE, 30);
                    g.setColor(Color.LIGHT_GRAY);
                    g.setFont(Fonts.SMALL);
                    g.drawString(this.controller.getLabel(word), Main.TASKBAR_SIZE, 50);
                    g.setFont(Fonts.NORMAL);
                },
                (previous, next) -> previous == null || previous.dictionary != next.dictionary ? next.dictionary.description : null,
                (word) -> word.word,
                (word) -> false,
                null
        );
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.invalid) {
            g.setColor(Colors.DARK_RED);
            g.fillRect(0, 480, 500, 32);
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 150, 480, 184, 32, "Woord al bekend");
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.list.setItems(this.controller.getWords());
        this.submitButton.setEnabled(this.controller.hasDictionary());
    }

    @Override
    public List<Widget> getChildren() {
        var dictionaries = this.controller.getDictionaries();
        return List.of(
            this.list,
            new InputWidget("WOORD", 0, 30, 400, 48, (value) -> this.word = value),
            this.submitButton,
            new DropdownWidget<>(dictionaries, "Taal", 400, 30, 80, 48, 10, this.controller::setDictionary)
        );
    }

    private void suggest() {
        this.invalid = !this.controller.addWord(this.word);
    }

    private void type(String input) {
        this.word = input;
        this.invalid = false;
    }
}
