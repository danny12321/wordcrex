package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.SuggestController;
import nl.avans.wordcrex.model.Word;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
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

    private String word = "";
    private boolean invalid;

    public SuggestView(SuggestController controller) {
        super(controller);
        this.list = new ListWidget<>(
                96,
                96,
                (g, word) -> {

                    g.setColor(Colors.DARKER_YELLOW);
                    g.fillRect(0, 100, 200, 50);

                    var metrics = g.getFontMetrics(g.getFont());


                    g.setColor(Color.WHITE);
                    g.drawString(word.dictionary.code, Main.TASKBAR_SIZE, 170);
                    g.drawString(word.state.toString(), 200, 170);
                    g.drawString(word.word, Main.FRAME_SIZE - Main.TASKBAR_SIZE * 2 - metrics.stringWidth(word.word), 170);



                    g.setColor(Colors.DARKERER_BLUE);
                    g.fillRect(0, 204, 480, 4);

                },
                (previous, next) -> null,
                (word) -> word.word,
                (word) -> false,
                null
        );
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.invalid) {
            g.setColor(Colors.DARK_RED);
            g.fillRect(64, 360, 184, 32);
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 64, 360, 184, 32, "Woord al bekend");
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public List<Widget> getChildren() {
        var dictionaries = this.controller.getDictionaries();
        return List.of(
            this.list,
            new InputWidget("WOORD", 0, 30, 400, 48, (value) -> this.word = value),
            new ButtonWidget("SUGGEREER", 0, 78, 480, 48, this::suggest),
            new DropdownWidget<>(dictionaries, "Taal", 400, 30, 80, 48, 10, this.controller::setDictionary)
        );
    }

    private void suggest() {
        this.invalid = this.controller.addWord(this.word);
    }
}
