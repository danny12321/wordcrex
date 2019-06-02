package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ApproveController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ApproveView extends View<ApproveController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    //private final ButtonWidget acceptButton = new ButtonWidget("Y", 220,200,40,40,this::accept);
    //private final ButtonWidget declineButton = new ButtonWidget("N", 260,200,40,40,this::decline);
    private int scroll;

    public ApproveView(ApproveController controller) {
        super(controller);
    }

    public void draw(Graphics2D g) {
        var index = new AtomicInteger();

        var words = this.controller.words;

        words.forEach((value) -> {
            var offset = index.getAndIncrement() * 88 - this.scroll;

            g.setColor(Color.WHITE);
            g.drawString(value.word, Main.TASKBAR_SIZE, 80 + offset);

            if (index.get() < words.size()) {
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(0, 114 + offset, 480, 4);
            }
        });
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.scrollbar
            //this.acceptButton,
            //this.declineButton
        );
    }
}
