package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ChatController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Assets;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ChatView extends View<ChatController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll, true);
    private final InputWidget input = new InputWidget("BERICHT", 48, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 96, 48, this.controller::setMessage);
    private final ButtonWidget sendMessage = new ButtonWidget(Assets.read("next"), null, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 40, Main.FRAME_SIZE - 40, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, this::sendMessage);

    private int scroll = 0;

    public ChatView(ChatController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var gap = 16;
        var size = 32;
        var maxBubbleSize = Main.FRAME_SIZE - Main.TASKBAR_SIZE - (gap * 4 + size * 2);

        var messages = this.controller.getMessages();
        var y = Main.TASKBAR_SIZE + gap;
        var contentHeight = messages.stream().mapToInt(m -> StringUtil.split(g, m.message, maxBubbleSize).size() * size + gap).sum() - Main.FRAME_SIZE + Main.TASKBAR_SIZE + 48 + gap;

        for (var i = 0; i < messages.size(); i++) {
            var userMessage = false;
            var x = gap;

            if (messages.get(i).username.equals(this.controller.getUsername())) {
                userMessage = true;
                x = Main.FRAME_SIZE - Main.TASKBAR_SIZE - size - gap;
            }

            if (!(i != 0 && messages.get(i - 1).username.equals(messages.get(i).username))) {
                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(x, y - contentHeight + this.scroll, size, size);
                g.setFont(Fonts.NORMAL);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, x, y - contentHeight + this.scroll, size, size, messages.get(i).username.substring(0, 1).toUpperCase());
            }

            var message = messages.get(i).message;
            var width = g.getFontMetrics().stringWidth(message);
            var height = g.getFontMetrics().stringWidth(message);

            if (width > maxBubbleSize) {
                width = maxBubbleSize;
            }

            var stringX = userMessage ? x - width - gap : x + size + gap;
            var lines = StringUtil.split(g, message, maxBubbleSize);

            for (var line : lines) {
                g.setColor(Colors.DARK_BLUE);
                g.fillRect(stringX - gap / 2, y - contentHeight + this.scroll, width + gap, size);

                g.setColor(Color.WHITE);
                g.drawString(line.trim(), stringX, y + height - contentHeight + this.scroll);

                y += size;
            }

            y += gap;
        }

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48);

        this.scrollbar.setHeight(y + gap);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.sendMessage.setEnabled(this.controller.canSend());
    }

    @Override
    public List<Widget> children() {
        return List.of(
            new ButtonWidget(Assets.read("back"), null, 8, Main.FRAME_SIZE - 40, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, this.controller::navigateGame),
            this.scrollbar,
            this.input,
            this.sendMessage
        );
    }

    private void sendMessage() {
        this.controller.sendMessage();
        this.input.clearInput();
    }
}
