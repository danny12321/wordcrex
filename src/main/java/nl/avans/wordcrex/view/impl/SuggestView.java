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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SuggestView extends View<SuggestController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);

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
            g.fillRect(64, 360, 184, 32);
            g.setColor(Color.WHITE);
            StringUtil.drawCenteredString(g, 64, 360, 184, 32, "Word already known");
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
    }

    @Override
    public List<Widget> getChildren() {
        LinkedHashMap<String, String> languages = new LinkedHashMap<>();
        languages.put("NL", "NL");
        languages.put("EN", "EN");

        return List.of(
            this.scrollbar,
            new InputWidget("Word", 0, 30, 400, 48, (value) -> this.word = value),
            new ButtonWidget("Suggest", 0, 78, 480, 48, this::Suggest),
            new DropdownWidget<>(languages, "Taal", 400, 30, 80, 48, this.controller::setLanguage)
        );
    }

    private void Suggest() {
        this.controller.addWord(this.word);
    }
}
