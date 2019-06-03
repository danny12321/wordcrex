package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.SuggestController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.DropdownWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SuggestView extends View<SuggestController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private final ButtonWidget submitButton = new ButtonWidget("SUGGEREER", 0, 78, 480, 48, this::suggest);
    private String word = "";
    private boolean invalid;
    private int scroll;

    public SuggestView(SuggestController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.invalid) {
            g.setColor(Colors.DARK_RED);
            g.fillRect(0, 480, 500, 32);
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 150, 480, 184, 32, "Woord al bekend");
        }

        var metrics = g.getFontMetrics(g.getFont());
        var index = new AtomicInteger();
        var words = this.controller.getWords();

        words.forEach((key, list) -> list.forEach((value) -> {
            var offset = index.getAndIncrement() * 88 - this.scroll;
            String state = value.state.toString();

            g.setColor(Color.WHITE);
            g.drawString(key, Main.TASKBAR_SIZE, 170 + offset);
            g.drawString(state, 200, 170 + offset);
            g.drawString(value.word, Main.FRAME_SIZE - Main.TASKBAR_SIZE * 2 - metrics.stringWidth(value.word), 170 + offset);

            if (index.get() < list.size()) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(0, 204 + offset, 480, 4);
            }
        }));

        this.scrollbar.setHeight(100 * 88 + 128);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.submitButton.setEnabled(this.controller.hasDictionary());
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.scrollbar,
            this.submitButton,
            new InputWidget("WOORD", 0, 30, 400, 48, this::type),
            new DropdownWidget<>(this.controller.getDictionaries(), "Taal", 400, 30, 80, 48, 10, this.controller::setDictionary)
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
